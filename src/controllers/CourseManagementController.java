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
import databases.CourseTrainDAO;
import javafx.stage.Stage;
import models.Course;
import models.CourseTrain;
import views.MenuSelectionView;

public class CourseManagementController {

    private final Stage stage;
    private final Connection connection;

    public CourseManagementController(Stage stage, Connection connection) {
        this.stage = stage;
        this.connection = connection;
    }

    public void extract(File file) throws IOException, SQLException {
        // Load the PDF document
        PDDocument document = PDDocument.load(file);
        for (int i = 0; i < document.getNumberOfPages(); i++) {

            PDFTextStripper textStripper = new PDFTextStripper();
            textStripper.setStartPage(i + 1);
            textStripper.setEndPage(i + 1);
            String pageText = textStripper.getText(document);

            Course course = new Course();
            boolean found = false;
            // 案名称
            Pattern pattern = Pattern.compile("［案名称］\\s*(.*?)\\s*［");
            Matcher matcher = pattern.matcher(pageText);
            if (matcher.find()) {
                course.setIssue(matcher.group(1));
                found = true;
            }

            // 発行日
            pattern = Pattern.compile("［発行日］\\s*([0-9]{4}/[0-9]{2}/[0-9]{2})");
            matcher = pattern.matcher(pageText);
            if (matcher.find()) {
                course.setIssue_date(matcher.group(1));
                found = true;
            }

            // 行路番号
            pattern = Pattern.compile("［行路番号］\\s*Ｃ\\s*([0-9０-９]+)\\s*([平土休日\\s]+)");
            matcher = pattern.matcher(pageText);
            if (matcher.find()) {

                String number = matcher.group(1);
                String type = matcher.group(2).replaceAll("\\s+", "");

                // TYPE 1:平日/2:土休日
                if (type.contains("平")) {
                    course.setDay_type(1); //
                } else {
                    course.setDay_type(2);
                }

                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                course.setUploaded_at(now.format(formatter));
                found = true;
            }

            // 行路面曜日
            pattern = Pattern.compile("［行路面曜日］\\s*(.*?)\\s*［");
            matcher = pattern.matcher(pageText);
            if (matcher.find()) {
                course.setDay_code(matcher.group(1));
                found = true;
            }

            if (found) {
                try {
                    connection.setAutoCommit(false);
                    CourseDAO course_dao = new CourseDAO(connection);
                    int course_id = course_dao.insert(course);

                    List<CourseTrain> course_train_list = new ArrayList<CourseTrain>();
                    String[] lines = pageText.split("\\R");

                    String position = null;
                    String crew_type = null;

                    for (String line : lines) {

                        Pattern p = Pattern.compile("^\\s*(\\d+)\\s+.*?(\\d+[A-ZＡ-ＺＭ])\\s+.*?(同電運転|便乗|改札|運転|同電便乗)");
                        Matcher m = p.matcher(line);
                        if (m.find()) {
                            position = m.group(1);
                            String train_number = m.group(2);
                            crew_type = m.group(3);

                            CourseTrain course_train = new CourseTrain();
                            course_train.setCourse_id(course_id);
                            course_train.setPosition(Integer.parseInt(position));
                            // 1:運転/2:運転以外
                            if (crew_type.equals("運転") || crew_type.equals("同電運転")) {
                                course_train.setCrew_type(1);
                            } else {
                                course_train.setCrew_type(2);
                            }
                            course_train.setTrain_number(train_number);

                            course_train_list.add(course_train);
                            continue;
                        }

                        // capture extra trains in 前運用 / 後運用 lines
//                        if (sequence != null && (line.contains("(前運用)") || line.contains("(後運用)"))) {
//                            p = Pattern.compile("(\\d+[A-ZＡ-Ｚ])");
//
//                            m = p.matcher(line);
//                            while (m.find()) {
//                                String train_number = m.group(1);
//                                CourseTrain course_train = new CourseTrain();
//                                course_train.setCourse_id(course_id);
//                                course_train.setSequence(Integer.parseInt(sequence));
//                                course_train.setCrew_type(crew_type);
//                                course_train.setTrain_number(train_number);
//
//                                course_train_list.add(course_train);
//                            }
//                        }
                    }

                    if (!course_train_list.isEmpty()) {
                        CourseTrainDAO course_train_dao = new CourseTrainDAO(connection);
                        course_train_dao.insertAll(course_train_list);
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

        }
        document.close();

//        try {
//            connection.setAutoCommit(false);
//            course_dao.insertAll(course_list);
//            connection.commit();
//        } catch (SQLException e) {
//            connection.rollback();
//            e.printStackTrace();
//            throw new SQLException(e);
//        } finally {
//            connection.setAutoCommit(true);
//        }
    }

    public void goBack() {
        new MenuSelectionView(stage, connection).show();
    }

}
