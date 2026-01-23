package databases;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteService {
    private String URL = "jdbc:sqlite:";
    private Connection connection = null;

    public SQLiteService() {
        String current_dir = System.getProperty("user.dir");
        String folder_name = current_dir.substring(current_dir.lastIndexOf(File.separator) + 1);

        String database_path = null;
        if (current_dir.startsWith("C:\\Program Files") || current_dir.startsWith("C:\\Program Files (x86)")) {
            database_path = System.getenv("APPDATA") + File.separator + folder_name + File.separator + "resources"
                    + File.separator + "databases";
        } else {
            database_path = current_dir + File.separator + "resources" + File.separator + "databases";
        }
        File database_dir = new File(database_path);
        if (!database_dir.exists()) {
            boolean dirCreated = database_dir.mkdirs();
            if (!dirCreated) {
                System.err.println("Failed to create AppData directory: " + database_dir);
                return;
            }
        }
        URL += database_path.replace("\\", "/") + "/localdb.db";
    }

    public Connection openConnection() {

        try {
            connection = DriverManager.getConnection(URL);
            createTables();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void dropTables() throws SQLException {
        Statement stmt = connection.createStatement();
        String query = "SELECT name FROM sqlite_master WHERE type='table';";
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            String tableName = rs.getString("name");
            if (!tableName.equals("sqlite_sequence")) {
                String dropSQL = "DROP TABLE IF EXISTS " + tableName + ";";
                stmt.executeUpdate(dropSQL);
                String resetAutoIncrementSQL = "DELETE FROM sqlite_sequence WHERE name='" + tableName + "'";
                stmt.executeUpdate(resetAutoIncrementSQL);

            }
        }

        System.out.println("All tables dropped successfully!");
    }

    public void createTables() throws SQLException {

        // 線区マスタ
        String create_route_sections_table = """
                CREATE TABLE IF NOT EXISTS route_sections (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    position INTEGER NOT NULL CHECK(position BETWEEN 0 AND 255),                            -- 順序
                    name TEXT NOT NULL CHECK(length(name) <= 80),                                           -- 線区名
                    letter TEXT CHECK(length(letter) <= 10),                                                -- 線区省略名
                    timetable_format_type INTEGER NOT NULL CHECK(timetable_format_type BETWEEN 1 AND 2),    -- 時刻表フォーマット種別
                    timetable_day_type INTEGER NOT NULL CHECK(timetable_day_type BETWEEN 0 AND 2),          -- 時刻表ダイヤ面種別
                    timetable_layout_type INTEGER NOT NULL CHECK(timetable_layout_type BETWEEN 1 AND 3),    -- 時刻表ページレイアウト種別
                    uploaded_at TEXT CHECK(uploaded_at GLOB '[0-9][0-9][0-9][0-9]/[0-9][0-9]/[0-9][0-9] [0-9][0-9]:[0-9][0-9]:[0-9][0-9]'),         -- アップロード日時
                    timetable_version TEXT CHECK(length(timetable_version) <= 20),                          -- 時刻表改正版
                    timetable_updated TEXT CHECK(length(timetable_updated) <= 40)                           -- 時刻表改正日
                    );
                """;

        // 列車テーブル
        String create_trains_table = """
                CREATE TABLE IF NOT EXISTS trains (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    route_section_id INTEGER NOT NULL,                                      -- 線区ID
                    train_number TEXT NOT NULL CHECK(length(train_number) <= 10),           -- 列車番号
                    bound_type INTEGER NOT NULL CHECK(bound_type BETWEEN 1 AND 2),          -- 上下別
                    day_type INTEGER NOT NULL CHECK(day_type BETWEEN 1 AND 2),              -- ダイヤ面種別
                    train_type INTEGER NOT NULL CHECK(train_type BETWEEN 1 AND 4),          -- 列車種別
                    first_station TEXT NOT NULL CHECK(length(first_station) <= 20),         -- 始発停車場
                    last_station TEXT NOT NULL CHECK(length(last_station) <= 20),           -- 終着停車場
                    supplement TEXT CHECK(length(supplement) <= 80),                        -- 補足
                FOREIGN KEY (route_section_id) REFERENCES route_sections(ID) ON DELETE CASCADE ON UPDATE CASCADE);
                """;

        // 列車－駅テーブル
        String create_train_stations_table = """
                CREATE TABLE IF NOT EXISTS train_stations (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    train_id INTEGER NOT NULL,                                                          -- 列車ID
                    station_position INTEGER NOT NULL CHECK(station_position BETWEEN 0 AND 255),        -- 駅番号
                    station_name TEXT NOT NULL CHECK(length(station_name) <= 20),                       -- 駅名
                    arrival_time TIME CHECK(arrival_time GLOB '[0-9][0-9]:[0-9][0-9]:[0-9][0-9]'),      -- 到着時刻
                    departure_time TIME CHECK(departure_time GLOB '[0-9][0-9]:[0-9][0-9]:[0-9][0-9]'),  -- 発車時刻
                    platform TEXT CHECK(length(platform) <= 10),                               -- 発着ホーム
                    stop_type INTEGER NOT NULL CHECK(stop_type BETWEEN 0 AND 2),                        -- 停車区分
                    FOREIGN KEY (train_id) REFERENCES trains(ID) ON DELETE CASCADE ON UPDATE CASCADE);
                """;

        // 行路テーブル
        String create_courses_table = """
                CREATE TABLE IF NOT EXISTS courses (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    day_type INTEGER NOT NULL CHECK(day_type BETWEEN 1 AND 2),              -- 平日休日区分
                    day_code TEXT NOT NULL CHECK (length(day_code) = 2),                    -- 行路面曜日
                    course_number TEXT NOT NULL CHECK(length(course_number) <= 10),         -- 行路番号
                    issue TEXT CHECK(length(issue) <= 100),                                 -- 案名称
                    issue_date  TEXT CHECK(length(issue_date) <= 20),                       -- 発行日
                    uploaded_at TEXT CHECK(uploaded_at GLOB '[0-9][0-9][0-9][0-9]/[0-9][0-9]/[0-9][0-9] [0-9][0-9]:[0-9][0-9]:[0-9][0-9]')  -- アップロード日時
                );
                """;

        // 行路ー列車テーブル
        String create_course_trains_table = """
                CREATE TABLE IF NOT EXISTS course_trains (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    course_id INTEGER NOT NULL,                                         -- 行路ID
                    position INTEGER NOT NULL CHECK(position BETWEEN 0 AND 255),        -- 順序
                    train_number TEXT NOT NULL CHECK(length(train_number) <= 10),       -- 列車番号
                    crew_type TEXT NOT NULL CHECK(crew_type BETWEEN 1 AND 2),           -- 乗務種別
                    direct_from TEXT CHECK(length(direct_from) <= 10),                  -- 直通元列車番号
                    direct_to TEXT CHECK(length(direct_to) <= 10),                      -- 直通先列車番号
                    FOREIGN KEY(course_id) REFERENCES courses(ID) ON DELETE CASCADE ON UPDATE CASCADE
                );
                """;

        // 駅設定マスタ
        String create_station_settings_table = """
                CREATE TABLE IF NOT EXISTS station_settings (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    station_name TEXT NOT NULL CHECK(length(station_name) <= 20),       -- 駅名
                    duration INTEGER NOT NULL CHECK(duration BETWEEN 0 AND 255),        -- 接続猶予時間（分）
                    extraction INTEGER NOT NULL CHECK(extraction BETWEEN 0 AND 255)     -- 接続抽出数
                );

                """;

        // ログテーブル
        String create_logs_table = """
                CREATE TABLE IF NOT EXISTS operation_logs (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    operation_type INTEGER NOT NULL CHECK(operation_type BETWEEN 1 AND 6),                      -- 操作種別
                    operated_at TEXT NOT NULL CHECK(operated_at GLOB '[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9] [0-9][0-9]:[0-9][0-9]:[0-9][0-9]')  -- 操作日時
                );

                """;

        Statement stmt = connection.createStatement();
        stmt.execute(create_route_sections_table);
        stmt.execute(create_trains_table);
        stmt.execute(create_train_stations_table);
        stmt.execute(create_courses_table);
        stmt.execute(create_course_trains_table);
        stmt.execute(create_station_settings_table);
        stmt.execute(create_logs_table);

        String insert_initial_data = """
                INSERT INTO route_sections
                    (position, name, letter, timetable_format_type, timetable_day_type, timetable_layout_type) VALUES
                    (1,'外房線','外',1,0,1),
                    (2,'東金線','東',1,0,3),
                    (3,'京葉線二俣支線含む（土休日）','京',2,2,1),
                    (4,'京葉線～高谷支線（土休日）','京',2,2,1),
                    (5,'京葉線二俣支線含む（平日）','京',2,1,1),
                    (6,'京葉線～高谷支線（平日）','京',2,1,1),
                    (7,'京葉線','京',1,0,1),
                    (8,'成田線 我孫子～成田','成',1,0,1),
                    (9,'総武快速線（土休日）','総',2,2,1),
                    (10,'総武快速線（平日）','総',2,1,1),
                    (11,'総武緩行線 定期特急','総',1,0,1),
                    (12,'総武快速線 定期特急','総',1,0,1),
                    (13,'総武快速線 回送 東京～千葉','総',1,0,1),
                    (14,'総武快速線 回送 幕張～千葉','総',1,0,3),
                    (15,'総武・成田線 千葉～成田空港','総',1,0,2),
                    (16,'総武本線 佐倉～銚子','総',1,0,1),
                    (17,'成田線 成田～銚子','成',1,0,1),
                    (18,'特急確定版用（緩行線）','特',1,0,1),
                    (19,'特急確定版用（東京～千葉）','特',1,0,2),
                    (20,'特急確定版用（千葉～空港）','特',1,0,1),
                    (21,'特急確定版用（佐倉～銚子）','特',1,0,1),
                    (22,'特急確定版用（東京～蘇我）','特',1,0,1),
                    (23,'特急確定版用（蘇我～安房鴨川）','特',1,0,1),
                    (24,'特急確定版用（蘇我～館山）','特',1,0,1),
                    (25,'特急確定版用予定臨（緩行線）','特',1,0,1),
                    (26,'特急確定版用予定臨（快速線）','特',1,0,1),
                    (27,'特急確定版用予定臨（京葉線）','特',1,0,1),
                    (28,'特急確定版用予定臨（外房線）','特',1,0,1),
                    (29,'特急確定版用予定臨（内房線）','特',1,0,1),
                    (30,'内房線','内',1,0,1),
                    (31,'久留里線','久',1,0,1);
                """;
        stmt.executeUpdate(insert_initial_data);

    }

//    private static boolean columnExists(String table_name, String column_name) {
//        try {
//            ResultSet rs = connection.getMetaData().getColumns(null, null, table_name, column_name);
//            return rs.next();
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
}
