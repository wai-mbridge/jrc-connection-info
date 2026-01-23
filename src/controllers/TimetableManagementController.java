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
                continue;
            }

            if (route_section.getTimetable_format_type() == 1) {
                // 定期列車時刻
                PDPage page = document.getPage(i);
                extractTrainByFormatType1(page, 0);

            } else if (route_section.getTimetable_format_type() == 2) {
                // Ｅ電標準時刻
                PDPage page = document.getPage(i);
                if (i == 28) {
                    System.out.println("reach");
                }
                extractTrainByFormatType2(page, route_section);
            }
        }
    }

    private void extractTrainByFormatType1(PDPage page, float initial) throws IOException {

        PDFTextStripperByArea textStripper = new PDFTextStripperByArea();

        float x = 53;
        float y = (initial == 0) ? 28 + initial : initial;
        float width = 46;
        float height = 28.7f;

        int max_row = 50;
        for (int i = 0; i <= max_row; i++) {

            if (i != 0) {
                y += height;
            }
            switch (i) {
            case 1:
                height = 22.3f;
                break;
            case 2:
                height = 10.7f;
                break;
            case 3:
                height = 12.3f;
                break;
            case 4:
                height = 28.3f;
                break;
            }
            if (i >= 5) {
                height = 22.7f;
            }

            for (int j = 0; j <= 7; j++) {

                if (j == 0) {
                    if (i == 2 || i == 3) {
                        x = 65;
                        width = 34;
                    } else {
                        x = 53;
                        width = 46;
                    }

                }
                if (j > 0) {
                    x += width;
                    width = 68;
                }

                if ((i == 2) && j == 0) {
                    Rectangle2D.Float rect = new Rectangle2D.Float(53, y, 12, 23);

                    textStripper.addRegion("squareRegion", rect);
                    textStripper.extractRegions(page);
                    String text = textStripper.getTextForRegion("squareRegion").trim();

                }
                Rectangle2D.Float rect = new Rectangle2D.Float(x, y, width, height);

                textStripper.addRegion("squareRegion", rect);
                textStripper.extractRegions(page);
                String text = textStripper.getTextForRegion("squareRegion").trim();

                if (j == 0 && (text.equals("") || text.equals("終着停車場"))) {
                    y += height;
                    max_row = i;
                    break;
                }
            }
        }

        // check next table exist
        Rectangle2D.Float rectangle = new Rectangle2D.Float(x, y + 11, width, height);

        textStripper.addRegion("squareRegion", rectangle);
        textStripper.extractRegions(page);
        String next = textStripper.getTextForRegion("squareRegion").trim();

        if (next.equals("記事")) {

            extractTrainByFormatType1(page, y + 11);
        }
    }

    private void extractTrainByFormatType2(PDPage page, RouteSection route_section) throws IOException, SQLException {

        PDFTextStripperByArea stripper = new PDFTextStripperByArea();

        // ダイヤ面種別
        int day_type = route_section.getTimetable_day_type();

        // 上下別
        String bound = PDFUtil.extractText(stripper, page, new Rectangle(380, 50, 30, 50));
        int boud_type = bound.equals("上り") ? 1 : 2;

        final float START_X = 45f;
        final float START_Y = 94f;
        final int MAX_COLS = 8;
        final int MAX_ROWS = 54;
        final float[] COL_WIDTHS = { 56.9f, 65.1f, 65.3f, 64.8f, 65.6f, 65.2f, 65.1f, 64.9f };

        List<String> stations = new ArrayList<String>();
        int last_data_row = -1;

        for (int col = 0; col < MAX_COLS; col++) {

            boolean data_exist = false;

            float x = START_X;
            float y = START_Y;

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

                float height = (row > 1 && row < 4) ? 12 : (row >= 4 ? 13 : 22);

                if (row > 0) {
                    y += height;
                }

                if (row == MAX_ROWS) {
                    y = 789.5f;
                    height = 22;
                }

                Rectangle2D.Float cell = new Rectangle2D.Float(x, y, COL_WIDTHS[col], height);
                String text = PDFUtil.extractText(stripper, page, cell).trim();

                if (row >= 4 && col == 0) {
                    if (text.isEmpty()) {
                        last_data_row = row - 1;
                        break;
                    }
                    stations.add(text.replace(" ", "").replace("　", ""));
                }
                if (col != 0) {
                    switch (row) {
                    case 0:
                        train.setFirst_station(text.replace("（", "").replace("）", "").replace(" ", "").replace("　", "")
                                .replace("\r\n", ""));
                        break;
                    case 1:
                        train.setTrain_number(text);
                        if (!text.isEmpty()) {
                            data_exist = true;
                        }
                        break;
                    case 2:
                        train.setTrain_type(ConstantUtil.getTrainType(text));
                        break;
                    case MAX_ROWS:
                        if (!text.isEmpty()) {
                            train.setLast_station(text.replace("（", "").replace("）", "").replace(" ", "")
                                    .replace("　", "").replace("\r\n", ","));
                        }
                        break;
                    }
                    if (row >= 4 && row <= last_data_row && data_exist && !text.isEmpty()) {

                        Matcher m = Pattern.compile("(\\d{1,2}:\\d{2}\\.\\d{2})(.*)$").matcher(text);
                        if (m.find()) {
                            String time = DateTimeUtil.changeTimeFormat(m.group(1)); // time
                            String after = m.group(2); // text after

                            TrainStation ts = row_station_map.get(row);

                            ts.setArrival_time(time);
                            ts.setDeparture_time(time);
                            ts.setPlatform(!after.isEmpty() ? after : null);

                            if (train.getFirst_station().isEmpty()) {
                                train.setFirst_station(ts.getStation_name());
                            }
                            train.setLast_station(ts.getStation_name());
                        }
                    }
                }

                if (last_data_row == row) {
                    row = MAX_ROWS - 1;
                }
            }

            if (data_exist) {
                try {
                    connection.setAutoCommit(false);

                    TrainDAO train_dao = new TrainDAO(connection);
                    train.setBound_type(boud_type);
                    train.setDay_type(day_type);
                    train.setRoute_section_id(route_section.getId());
                    int train_id = train_dao.insert(train);

                    row_station_map.forEach((row, station) -> {
                        if ("↓".equals(station.getPlatform()) || station.getArrival_time() == null) {
                            station.setStop_type(0);
                            station.setPlatform(null);
                        } else if (train.getLast_station().equals(station.getStation_name())) {
                            station.setStop_type(2);
                        } else {
                            station.setStop_type(1);
                        }
                        station.setTrain_id(train_id);
                    });

                    TrainStationDAO train_station_dao = new TrainStationDAO(connection);
                    train_station_dao.insertAll(new ArrayList<>(row_station_map.values()));

                    connection.commit();
                } catch (SQLException e) {
                    connection.rollback();
                    e.printStackTrace();
                    throw new SQLException(e);
                } finally {
                    connection.setAutoCommit(true);
                }

            }
        }

    }

}
