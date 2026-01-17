package views;

import java.io.InputStream;
import java.sql.Connection;

import controllers.MenuSelectionController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MenuSelectionView {

    private final Stage stage;
    private final Connection connection;

    public MenuSelectionView(Stage stage, Connection connection) {
        this.stage = stage;
        this.connection = connection;
    }

    public void show() {
        InputStream imageStream = getClass().getClassLoader().getResourceAsStream("icons/jr.jpg");
        Image icon = new Image(imageStream);
        stage.getIcons().add(icon);

        stage.setTitle("メニュー");

        Button timetable_btn = new Button("時刻表管理");
        timetable_btn.setMaxWidth(Double.MAX_VALUE);

        Button additional_processing_btn = new Button("補足処理");
        additional_processing_btn.setMaxWidth(Double.MAX_VALUE);

        Button course_management_btn = new Button("行路管理");
        course_management_btn.setMaxWidth(Double.MAX_VALUE);

        Button station_management_btn = new Button("駅設定");
        station_management_btn.setMaxWidth(Double.MAX_VALUE);

        Button info_card_creation_btn = new Button("案内カード作成");
        info_card_creation_btn.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(timetable_btn, Priority.ALWAYS);
        HBox.setHgrow(additional_processing_btn, Priority.ALWAYS);
        HBox.setHgrow(course_management_btn, Priority.ALWAYS);
        HBox.setHgrow(station_management_btn, Priority.ALWAYS);
        HBox.setHgrow(info_card_creation_btn, Priority.ALWAYS);

        HBox timetable_btn_box = new HBox(timetable_btn);
        timetable_btn_box.setAlignment(Pos.CENTER);

        HBox additional_processing_btn_box = new HBox(additional_processing_btn);
        additional_processing_btn_box.setAlignment(Pos.CENTER);

        HBox course_management_btn_box = new HBox(course_management_btn);
        course_management_btn_box.setAlignment(Pos.CENTER);

        HBox station_management_btn_box = new HBox(station_management_btn);
        station_management_btn_box.setAlignment(Pos.CENTER);

        HBox info_card_creation_btn_box = new HBox(info_card_creation_btn);
        info_card_creation_btn_box.setAlignment(Pos.CENTER);

        VBox root = new VBox(10, timetable_btn_box, additional_processing_btn_box, course_management_btn_box,
                station_management_btn_box, info_card_creation_btn_box);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER_LEFT);

        MenuSelectionController controller = new MenuSelectionController(stage, connection);

        timetable_btn.setOnAction(e -> {
            controller.openTimetableManagement();
        });

        additional_processing_btn.setOnAction(e -> {
            controller.openAdditionalProcessing();
        });

        course_management_btn.setOnAction(e -> {
            controller.openCourseManagement();
        });

        station_management_btn.setOnAction(e -> {
            controller.openStationManagement();
        });

        info_card_creation_btn.setOnAction(e -> {
            controller.openInfoCardCreation();
        });

        Scene scene = new Scene(root, 500, 200);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

}
