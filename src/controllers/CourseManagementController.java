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

import databases.CourseDAO;
import javafx.stage.Stage;
import models.Course;
import views.MenuSelectionView;

public class CourseManagementController {

    private final Stage stage;
    private final Connection connection;

    public CourseManagementController(Stage stage, Connection connection) {
        this.stage = stage;
        this.connection = connection;
    }

    public void extract(File file) throws IOException, SQLException {
        List<Course> course_list = new ArrayList<Course>();
        // Load the PDF document
        PDDocument document = PDDocument.load(file);
        for (int i = 0; i < document.getNumberOfPages(); i++) {

            PDFTextStripper textStripper = new PDFTextStripper();
            textStripper.setStartPage(i + 1);
            textStripper.setEndPage(i + 1);
            String pageText = textStripper.getText(document);

            Course course = new Course();
            // 案名称
            Pattern pattern = Pattern.compile("［案名称］\\s*(.*?)\\s*［");
            Matcher matcher = pattern.matcher(pageText);
            if (matcher.find()) {
                course.setPlan_name(matcher.group(1));
            }

            // 発行日
            pattern = Pattern.compile("［発行日］\\s*([0-9]{4}/[0-9]{2}/[0-9]{2})");
            matcher = pattern.matcher(pageText);
            if (matcher.find()) {
                course.setIssue_date(matcher.group(1));
            }

            // 行路番号
            pattern = Pattern.compile("［行路番号］\\s*Ｃ\\s*([0-9０-９]+)\\s*([平土休日\\s]+)");
            matcher = pattern.matcher(pageText);
            if (matcher.find()) {

                String number = matcher.group(1); // 174
                String type = matcher.group(2).replaceAll("\\s+", ""); // 土土 / 平平 / 平

                // 土 → 休
                type = type.replace("土", "休");
                String route_number = type + " " + number;

                if (type.contains("平")) {
                    course.setWeekday_holiday_type("平日");
                } else {
                    course.setWeekday_holiday_type("休日");
                }
                course.setRoute_number(route_number);

                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                course.setUploaded_at(now.format(formatter));
            }
            course_list.add(course);
        }
        document.close();

        try {
            connection.setAutoCommit(false);
            CourseDAO course_dao = new CourseDAO(connection);
            course_dao.insertAll(course_list);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
            throw new SQLException(e);
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public void goBack() {
        new MenuSelectionView(stage, connection).show();
    }

}
