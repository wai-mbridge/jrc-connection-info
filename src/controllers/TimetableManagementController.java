package controllers;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import databases.RouteSectionDAO;
import databases.TrainDAO;
import databases.TrainStationDAO;
import javafx.stage.Stage;
import models.RouteSection;
import models.Train;
import models.TrainStation;
import utils.ConstantUtil;
import utils.DateTimeUtil;
import utils.PDFUtil;
import views.MenuSelectionView;

public class TimetableManagementController {
    private final Stage stage;
    private final Connection connection;
    private int hour = 0;

    public TimetableManagementController(Stage stage, Connection connection) {
        this.stage = stage;
        this.connection = connection;
    }

    public List<RouteSection> getAllDatas() {
        List<RouteSection> data = new ArrayList<RouteSection>();
        try {
            RouteSectionDAO dao = new RouteSectionDAO(connection);
            data = dao.all();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public void goBack() {
        new MenuSelectionView(stage, connection).show();
    }

    public void extract(File file) throws IOException, SQLException {

        // Get user's Downloads folder
        String userHome = System.getProperty("user.home");
        File downloadsDir = new File(userHome, "Downloads");

        // Make sure the directory exists
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs();
        }

        PDDocument document = PDDocument.load(file);
        RouteSectionDAO route_section_dao = new RouteSectionDAO(connection);

        Map<String, Object> route_section_data = new HashMap<>();
        RouteSection route_section = new RouteSection();

        List<RouteSection> route_section_list = new ArrayList<RouteSection>();
        List<Train> train_list = new ArrayList<Train>();
        for (int i = 0; i < document.getNumberOfPages(); i++) {

            PDFTextStripper textStripper = new PDFTextStripper();
            textStripper.setStartPage(i + 1);
            textStripper.setEndPage(i + 1);
            String pageText = textStripper.getText(document);
            if (i == 0) {
                Pattern p = Pattern.compile("(第[０-９0-9]+版)");
                Matcher m = p.matcher(pageText);

                if (m.find()) {
                    // 改正版
                    route_section_data.put("timetable_version", m.group());
                }
                continue;
            }
            if (pageText.contains("作成日")) {
                String[] lines = pageText.split("\\R");

                // 改正日
                route_section_data.put("timetable_updated", DateTimeUtil.changeFormat(lines[0]));

                // アップロード日時
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                route_section_data.put("uploaded_at", now.format(formatter));

                // 既存のデータを取得
                String name = lines[2];
                route_section = route_section_dao.upsertByName(name, route_section_data);
                route_section_list.add(route_section);
                continue;
            }

            if (route_section.getTimetable_format_type() == 1) {
                // 定期列車時刻
                PDPage page = document.getPage(i);
                train_list.addAll(extractTrainByFormatType1(page, route_section));

            } else if (route_section.getTimetable_format_type() == 2) {
                // Ｅ電標準時刻
                PDPage page = document.getPage(i);
                train_list.addAll(extractTrainByFormatType2(page, route_section));
            }
        }

        document.close();
        try {
            connection.setAutoCommit(false);

            TrainDAO train_dao = new TrainDAO(connection);
            TrainStationDAO station_dao = new TrainStationDAO(connection);

            for (RouteSection section : route_section_list) {
                train_dao.deleteBy("route_section_id", section.getId());
            }
            for (Train train : train_list) {
                int train_id = train_dao.insert(train);

                train.getTrain_stations().forEach(s -> s.setTrain_id(train_id));
                station_dao.insertAll(train.getTrain_stations());
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
            throw new SQLException(e);
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private List<Train> extractTrainByFormatType1(PDPage page, RouteSection route_section) throws IOException {

        PDFTextStripperByArea textStripper = new PDFTextStripperByArea();

        int layout_type = route_section.getTimetable_layout_type();

        final int MAX_COLS = 8;
        final int MAX_ROWS = layout_type == 1 ? 32 : (layout_type == 2 ? 16 : 10);

        List<Train> train_list = new ArrayList<>();

        // Define starting Y positions for each table based on layout type
        float[] start_y_positions;
        switch (layout_type) {
        case 1:
            start_y_positions = new float[] { 28f }; // Single table
            break;
        case 2:
            start_y_positions = new float[] { 28f, 435.6f }; // Two tables
            break;
        case 3:
            start_y_positions = new float[] { 28f, 277.9f, 527.55f }; // Three tables
            break;
        default:
            start_y_positions = new float[] { 28f }; // Default to single table
            break;
        }
        // Process each table
        for (int table_index = 0; table_index < start_y_positions.length; table_index++) {

            final float START_Y = start_y_positions[table_index];

            List<String> stations = new ArrayList<>();
            int bound_type = 0;
            int last_data_row = MAX_ROWS;

            for (int col = 0; col < MAX_COLS; col++) {

                float y = START_Y;
                float height = 0;

                float START_X = (col == 0) ? 53.7f : 53.7f + 44.7f + (col - 1) * 68f;
                float base_width = (col == 0) ? 44.7f : 68f;

                Map<Integer, TrainStation> row_station_map = new LinkedHashMap<>();
                Train train = new Train();
                boolean data_exist = false;
                int day_type = 0;

                if (col != 0) {
                    for (int i = 0; i < stations.size(); i++) {
                        TrainStation ts = new TrainStation();
                        ts.setStation_position(i + 1);
                        ts.setStation_name(stations.get(i));
                        row_station_map.put(i + 5, ts);
                    }
                }

                for (int row = 0; row <= last_data_row; row++) {

                    if (row > 0) {
                        y += height;
                    }

                    // row height
                    switch (row) {
                    case 0 -> height = 28.7f;
                    case 1 -> height = 22.3f;
                    case 2 -> height = 10.7f;
                    case 3 -> height = 12.3f;
                    case 4 -> height = 28.3f;
                    default -> height = 22.7f;
                    }

                    float x = START_X;
                    float width = base_width;

                    // column 0 special narrow cells for rows 2 & 3
                    if (col == 0 && (row == 2 || row == 3)) {
                        x = 65f;
                        width = 34f;
                    }

                    Rectangle2D.Float rect = new Rectangle2D.Float(x, y, width, height);
                    String text = PDFUtil.extractText(textStripper, page, rect).trim();

                    /* ---------- Column 0 ---------- */
                    if (col == 0) {

                        if (row == 2) {
                            Rectangle2D.Float bRect = new Rectangle2D.Float(53f, y, 12f, 23f);
                            String b = PDFUtil.extractText(textStripper, page, bRect).trim().replace("\r\n", "");
                            if (b.isEmpty()) {
                                break;
                            }
                            bound_type = ConstantUtil.getBoundType(b);
                        }

                        if (row >= 5 && row < last_data_row && !text.isEmpty()) {
                            if (text.equals("終着停車場")) {
                                last_data_row = row;
                                break;
                            }
                            stations.add(text.replace(" ", "").replace("　", ""));
                        }

                        continue;
                    }

                    /* ---------- Other columns ---------- */
                    switch (row) {
                    case 0:
                        if (text.contains("除く土休日")) {
                            day_type = 1;
                        } else if (text.contains("限る土休日")) {
                            day_type = 2;
                        }
                    case 1:
                        train.setFirst_station(reformFirstLastStationName(text));
                        break;
                    case 2:
                        train.setTrain_type(ConstantUtil.getTrainType(text));
                        break;
                    case 3:
                        train.setTrain_number(text);
                        data_exist = !text.isEmpty();
                        break;
                    default:
                        if (row == last_data_row) {
                            if (!text.isEmpty()) {
                                train.setLast_station(reformFirstLastStationName(text));
                            }
                        }
                    }

                    if (row >= 5 && row < last_data_row && data_exist && !text.isEmpty() && !text.equals("〔")
                            && !text.equals("〕") && !text.equals("‖")) {

                        // 0 => 列車交換
                        // 2 => 停車
                        int stop_type = text.contains("〕") ? 0 : 2;

//                        if (text.equals("‖")) {
//                            stop_type = 0;
//                        } else {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm[ ss]");

                        String arrival = PDFUtil
                                .extractText(textStripper, page, new Rectangle2D.Float(x + 4f, y, 41f, height / 2))
                                .trim().replace("\r\n", "");
                        String departure = PDFUtil.extractText(textStripper, page,
                                new Rectangle2D.Float(x + 4f, y + height / 2, 41f, height / 2));

                        String arrival_time = null;
                        String departure_time = null;

                        if (arrival.isEmpty()) {
                            // 始発
                            departure_time = getTimeWithFormat(departure, formatter);
                            stop_type = 1;
                        } else if (arrival.equals("…")) {
                            // 通過
                            departure_time = getTimeWithFormat(departure, formatter);
                            stop_type = 0;
                        } else if ("＝".equals(departure)) {
                            // 終着
                            arrival_time = getTimeWithFormat(arrival, formatter);
                            stop_type = 3;
                        } else {
                            arrival_time = getTimeWithFormat(arrival, formatter);
                            departure_time = getTimeWithFormat(departure, formatter);
                        }

                        String platform = PDFUtil
                                .extractText(textStripper, page, new Rectangle2D.Float(x + 45f, y, 13f, height)).trim()
                                .replace("\r\n", "").replace("（", "").replace("）", "");
//                        }

                        TrainStation ts = row_station_map.get(row);
                        if (ts == null)
                            continue;

                        ts.setArrival_time(arrival_time);
                        ts.setDeparture_time(departure_time);
                        ts.setStop_type(stop_type);
                        if (!platform.isEmpty()) {
                            ts.setPlatform(platform);
                        }

                        if (train.getFirst_station().isEmpty() && departure_time != null) {
                            train.setFirst_station(ts.getStation_name());
                        }
                        train.setLast_station(ts.getStation_name());
                    }
                }

                if (data_exist) {
                    train.setBound_type(bound_type);
                    train.setRoute_section_id(route_section.getId());
                    train.setTrain_stations(new ArrayList<>(row_station_map.values()));
                    if (day_type == 0) {
                        train.setDay_type(1);
                        train_list.add(train);

                        Train new_train = new Train(train);
                        new_train.setDay_type(2);
                        train_list.add(new_train);

                        System.out.println("reach");
                    } else {
                        train.setDay_type(day_type);
                        train_list.add(train);
                    }
                }
            }
        }

        return train_list;

    }

    private List<Train> extractTrainByFormatType2(PDPage page, RouteSection route_section)
            throws IOException, SQLException {

        PDFTextStripperByArea textStripper = new PDFTextStripperByArea();

        // ダイヤ面種別
        int day_type = route_section.getTimetable_day_type();

        // 上下別
        String bound = PDFUtil.extractText(textStripper, page, new Rectangle(380, 50, 30, 50));
        int boud_type = ConstantUtil.getBoundType(bound);

        final float START_X = 45f;
        final float START_Y = 94f;
        final int MAX_COLS = 8;
        final int MAX_ROWS = 54;
        final float[] COL_WIDTHS = { 56.9f, 65.1f, 65.3f, 64.8f, 65.6f, 65.2f, 65.1f, 64.9f };

        List<String> stations = new ArrayList<String>();
        List<Train> train_list = new ArrayList<Train>();
        int last_data_row = -1;

        for (int col = 0; col < MAX_COLS; col++) {

            boolean data_exist = false;

            float x = START_X;
            float y = START_Y;
            float height = 0;

            for (int i = 0; i < col; i++) {
                x += COL_WIDTHS[i];
            }

            Map<Integer, TrainStation> row_station_map = new LinkedHashMap<>();
            Train train = new Train();
            if (col != 0) {
                for (int i = 0; i < stations.size(); i++) {
                    String name = stations.get(i);

                    TrainStation ts = new TrainStation();
                    ts.setStation_position(i + 1);
                    ts.setStation_name(name);
                    row_station_map.put(i + 4, ts);
                }
            }

            for (int row = 0; row <= MAX_ROWS; row++) {

                height = (row > 1 && row < 4) ? 12 : (row >= 4 ? 13 : 22);

                if (row > 0) {
                    y += height;
                }

                if (row == MAX_ROWS) {
                    y = 789.5f;
                    height = 22;
                }

                Rectangle2D.Float cell = new Rectangle2D.Float(x, y, COL_WIDTHS[col], height);
                String text = PDFUtil.extractText(textStripper, page, cell).trim();

                /* ---------- Column 0 ---------- */
                if (row >= 4 && col == 0) {
                    if (text.isEmpty()) {
                        last_data_row = row - 1;
                        break;
                    }
                    stations.add(text.replace(" ", "").replace("　", ""));
                }

                /* ---------- Other columns ---------- */
                if (col != 0) {

                    switch (row) {
                    case 0:
                        train.setFirst_station(reformFirstLastStationName(text));
                        break;
                    case 1:
                        train.setTrain_number(text);
                        data_exist = !text.isEmpty();
                        break;
                    case 2:
                        if (train.getTrain_number().startsWith("回")) {
                            train.setTrain_type(6);
                        } else {
                            train.setTrain_type(ConstantUtil.getTrainType(text));
                        }
                        break;
                    case MAX_ROWS:
                        if (!text.isEmpty()) {
                            train.setLast_station(reformFirstLastStationName(text));
                        }
                        break;
                    }
                    if (row >= 4 && row <= last_data_row && data_exist && !text.isEmpty()) {

                        String arrow_check = PDFUtil
                                .extractText(textStripper, page, new Rectangle2D.Float(x, y, 8.5f, height)).trim();

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm.ss");

                        String time = PDFUtil
                                .extractText(textStripper, page, new Rectangle2D.Float(x + 8.5f, y, 39.7f, height))
                                .trim().replace("\r\n", "");
                        time = DateTimeUtil.changeTimeFormat(time, formatter);

                        String platform = PDFUtil
                                .extractText(textStripper, page, new Rectangle2D.Float(x + 48.2f, y, 17f, height))
                                .trim();

                        TrainStation ts = row_station_map.get(row);
                        if (ts == null)
                            continue;

                        ts.setArrival_time(time);
                        ts.setDeparture_time(time);
                        if (!platform.isEmpty()) {
                            ts.setPlatform(platform);
                        }

                        // 0 => 列車交換
                        // 2 => 停車
                        ts.setStop_type("↓".equals(arrow_check) ? 0 : 2);

                        if (train.getFirst_station().isEmpty()) {
                            train.setFirst_station(ts.getStation_name());
                        }
                        train.setLast_station(ts.getStation_name());
                    }
                }

                if (last_data_row == row) {
                    row = MAX_ROWS - 1;
                }
            }

            if (data_exist) {
                train.setBound_type(boud_type);
                train.setDay_type(day_type);
                train.setRoute_section_id(route_section.getId());

                List<TrainStation> train_stations = new ArrayList<TrainStation>();
                row_station_map.forEach((row, station) -> {
                    if (train.getFirst_station().equals(station.getStation_name())) {
                        // 始発
                        station.setStop_type(1);
                    } else if (train.getLast_station().equals(station.getStation_name())) {
                        // 終着
                        station.setStop_type(3);
                    }

                    train_stations.add(station);
                });
                train.setTrain_stations(train_stations);
                train_list.add(train);
            }
        }

        return train_list;

    }

    private String reformFirstLastStationName(String station_name) {
        return station_name.replace("（", "").replace("）", "").replace(" ", "").replace("　", "").replace("\r\n", ",");
    }

    private String getTimeWithFormat(String time, DateTimeFormatter formatter) {

        if (DateTimeUtil.checkHourContain(time)) {
            hour = Integer.parseInt(time.split(":")[0]);
            return DateTimeUtil.changeTimeFormat(time, formatter);
        }
        return DateTimeUtil.changeTimeFormat(hour + ":" + time, formatter);
    }

//    private String extractTimeForType2(PDFTextStripperByArea stripper, PDPage page, Rectangle2D.Float rect,
//            DateTimeFormatter formatter) {
//        return null;
//    }

}
