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
                    ID INTEGER PRIMARY KEY AUTOINCREMENT,               -- ID
                    sequence INTEGER NOT NULL,                     -- 順序
                    name TEXT NOT NULL,                                 -- 名称
                    short_name TEXT,                                    -- 省略名
                    timetable_format TEXT,                              -- 時刻表フォーマット
                    timetable_diagram TEXT,                             -- 時刻表ダイヤ面
                    timetable_page_layout TEXT,                         -- 時刻表ページレイアウト
                    uploaded_at TEXT DEFAULT CURRENT_TIMESTAMP,         -- アップロード日時
                    timetable_revision_version TEXT,                    -- 時刻表改正版
                    timetable_revision_date TEXT                        -- 時刻表改正日
                    );
                """;

        // 列車テーブル
        String create_trains_table = """
                CREATE TABLE IF NOT EXISTS trains (
                    ID INTEGER PRIMARY KEY AUTOINCREMENT,      -- ID
                    route_section_id INTEGER NOT NULL,         -- 線区ID
                    train_number TEXT NOT NULL,                -- 列車番号
                    weekday_holiday_type TEXT,                 -- 平日休日区分
                    up_down_type TEXT,                         -- 上下区分
                    train_type TEXT,                           -- 列車種別
                    start_station TEXT,                        -- 始発停車場
                    end_station TEXT,                          -- 終着停車場
                    remark TEXT,                               -- 補足
                FOREIGN KEY (route_section_id) REFERENCES route_sections(ID) ON DELETE CASCADE ON UPDATE CASCADE);
                """;

        // 列車－駅テーブル
        String create_train_stations_table = """
                CREATE TABLE IF NOT EXISTS train_stations (
                    ID INTEGER PRIMARY KEY AUTOINCREMENT,       -- ID
                    train_id INTEGER NOT NULL,                  -- 列車ID
                    station_number TEXT NOT NULL,               -- 駅番号
                    station_name TEXT NOT NULL,                 -- 駅名
                    arrival_time TEXT,                          -- 到着時刻
                    departure_time TEXT,                        -- 発車時刻
                    platform TEXT,                              -- 発着ホーム
                    stop_type TEXT,                             -- 停車区分
                    FOREIGN KEY (train_id) REFERENCES trains(ID) ON DELETE CASCADE ON UPDATE CASCADE);
                """;

        // 行路テーブル
        String create_courses_table = """
                CREATE TABLE IF NOT EXISTS courses (
                    ID INTEGER PRIMARY KEY AUTOINCREMENT,           -- ID
                    weekday_holiday_type TEXT,                      -- 平日休日区分
                    route_day_of_week TEXT,                         -- 行路面曜日
                    route_number TEXT,                              -- 行路番号
                    plan_name TEXT,                                 -- 案名称
                    issue_date DATE,                                -- 発行日
                    uploaded_at DATETIME DEFAULT CURRENT_TIMESTAMP  -- アップロード日時
                );
                """;

        // 行路ー列車テーブル
        String create_course_trains_table = """
                CREATE TABLE IF NOT EXISTS course_trains (
                    ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    course_id INTEGER NOT NULL,                 -- 行路ID
                    train_number TEXT,                          -- 列車番号
                    sequence INTEGER,                           -- 順序
                    crew_type TEXT,                             -- 乗務種別
                    direct_source_train_number TEXT,            -- 直通元列車番号
                    direct_destination__train_number TEXT,      -- 直通先列車番号
                    FOREIGN KEY(course_id) REFERENCES courses(ID) ON DELETE CASCADE ON UPDATE CASCADE
                );
                """;

        // 駅設定マスタ
        String create_station_settings_table = """
                CREATE TABLE IF NOT EXISTS station_settings (
                    ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    station_name TEXT NOT NULL,          -- 駅名
                    connection_grace_time INTEGER,       -- 接続猶予時間（分）
                    connection_extract_count INTEGER     -- 接続抽出数
                );

                """;

        // ログテーブル
        String create_logs_table = """
                CREATE TABLE IF NOT EXISTS logs (
                    ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    action_type TEXT NOT NULL,                      -- 操作区分
                    executed_at DATETIME DEFAULT CURRENT_TIMESTAMP  -- 実行日時
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
