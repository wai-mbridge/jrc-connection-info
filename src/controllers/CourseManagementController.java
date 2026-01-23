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

        int day_type = 0;
        String file_name = file.getName();
        if (file_name.contains("平")) {
            day_type = 1;
        } else if (file_name.contains("休")) {
            day_type = 2;
        }

        if (day_type == 0) {
            return;
        }

        try {

            connection.setAutoCommit(false);
            // 既存レコードを削除
            CourseDAO course_dao = new CourseDAO(connection);
            course_dao.deleteBy("day_type", day_type);

            PDDocument document = PDDocument.load(file);

            for (int i = 0; i < document.getNumberOfPages(); i++) {

                PDFTextStripper textStripper = new PDFTextStripper();
                textStripper.setStartPage(i + 1);
                textStripper.setEndPage(i + 1);
                String pageText = textStripper.getText(document);

                // 行路データ取込
                Course course = extractCourse(pageText, day_type);

                if (course == null) {
                    break;
                }

                // 行路データ登録
                int course_id = course_dao.insert(course);

                // 行路ー列車データ取込
                List<CourseTrain> course_train_list = extractCourseTrain(pageText, course_id);

                if (!course_train_list.isEmpty()) {
                    CourseTrainDAO course_train_dao = new CourseTrainDAO(connection);
                    course_train_dao.insertAll(course_train_list);
                }

            }
            document.close();

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

    private boolean checkDirectService(String train1, String train2) {
        return train1.length() >= 4 && train2.length() >= 4
                && train1.substring(train1.length() - 4).equals(train2.substring(train2.length() - 4));

    }

    private Course extractCourse(String pageText, int day_type) {

        Pattern pattern = Pattern.compile(
                "［案名称］\\s*(.+?)\\s+［計画状態］[\\s\\S]*?［発行日］\\s*([0-9/]+)[\\s\\S]*?［行路番号］\\s*Ｃ\\s*([0-9０-９]+)[\\s\\S]*?［行路面曜日］\\s*([0-9０-９]+)");
        Matcher matcher = pattern.matcher(pageText);
        if (matcher.find()) {
            Course course = new Course();

            // 案名称
            course.setIssue(matcher.group(1));
            // 発行日
            course.setIssue_date(matcher.group(2));
            // 行路番号
            course.setCourse_number(matcher.group(3));
            // 行路面曜日
            course.setDay_code(matcher.group(4));
            // 平日休日区分
            course.setDay_type(day_type);
            // アップロード日時
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            course.setUploaded_at(now.format(formatter));

            return course;
        }
        return null;

    }

    private List<CourseTrain> extractCourseTrain(String pageText, int course_id) {
        List<CourseTrain> course_train_list = new ArrayList<CourseTrain>();
        Pattern pattern = Pattern.compile("＜乗務列車情報＞([\\s\\S]*?)＜乗務列車付帯情報［駅作業情報］＞");
        Matcher matcher = pattern.matcher(pageText);

        if (matcher.find()) {
            String[] lines = matcher.group(1).split("\\R");

            CourseTrain previous_course_train = null;

            for (String line : lines) {

                Pattern p = Pattern.compile("^\\s*(\\d+)\\s+.*?(\\d+[A-ZＡ-ＺＭ])\\s+.*?(同電運転|便乗|改札|運転|同電便乗)");
                Matcher m = p.matcher(line);
                if (m.find()) {
                    int position = Integer.parseInt(m.group(1));
                    String train_number = m.group(2);
                    // 1:運転/2:運転以外
                    int crew_type = m.group(3).contains("運転") ? 1 : 2;

                    CourseTrain course_train = new CourseTrain(course_id, position, train_number, crew_type);

                    if (previous_course_train == null) {
                        previous_course_train = course_train;
                    } else {
                        String previous_train_number = previous_course_train.getTrain_number();
                        String current_train_number = course_train.getTrain_number();

                        if (checkDirectService(previous_train_number, current_train_number)) {
                            previous_course_train.setDirect_to(current_train_number);
                            course_train.setDirect_from(previous_train_number);
                        }
                        previous_course_train = course_train;
                    }

                    course_train_list.add(course_train);
                }

            }

        }
        return course_train_list;
    }

}
