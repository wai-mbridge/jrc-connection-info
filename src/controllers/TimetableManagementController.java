package controllers;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import databases.RouteSectionDAO;
import javafx.stage.Stage;
import models.RouteSection;
import utils.DateUtil;
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
        PDDocument document = PDDocument.load(file);

//        int timetable_format_type;
//        String timetable_version;
        String timetable_updated = "";
        RouteSection route_section = new RouteSection();
        for (int i = 0; i < document.getNumberOfPages(); i++) {

            PDFTextStripper textStripper = new PDFTextStripper();
            textStripper.setStartPage(i + 1);
            textStripper.setEndPage(i + 1);
            String pageText = textStripper.getText(document);
            if (i == 0) {
                Pattern p = Pattern.compile("(?ms)^.*?\\R(.+?)\\s*改正\\s*\\R+\\s*(.+?)\\s*\\R+\\s*(第[０-９0-9]+版)");
                Matcher m = p.matcher(pageText);

                if (m.find()) {
                    timetable_updated = m.group(1);
                    route_section.setTimetable_updated(DateUtil.changeFormat(timetable_updated));
                    route_section.setTimetable_format_type(getTimetableFormat(m.group(2)));
                    route_section.setTimetable_version(m.group(3));

                    String uploaded_at = LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY/MM/DD HH:mm:ss"));
                    route_section.setUploaded_at(uploaded_at);
                }
                continue;
            }
            if (pageText.startsWith(timetable_updated)) {
                String[] lines = pageText.split("\\R");
                route_section.setName(lines[2]);
                route_section.setLetter(lines[2].substring(0, 1));
                int timetable_day_type;
                if (lines[3].contains("全")) {
                    timetable_day_type = 0;
                } else if (lines[3].contains("平")) {
                    timetable_day_type = 1;
                } else {
                    timetable_day_type = 2;
                }
                route_section.setTimetable_day_type(timetable_day_type);
                route_section.setTimetable_layout_type(1);

                RouteSectionDAO route_section_dao = new RouteSectionDAO(connection);
                int route_section_id = route_section_dao.insert(route_section);

            }
        }
    }

    private int getTimetableFormat(String text) {
        String[] lines = text.split("\\R");
        String format = lines[0].trim();
        if (lines.length == 2) {
            format = lines[1].trim();
        }
        return format.equals("定期列車時刻") ? 1 : 2;
    }
}
