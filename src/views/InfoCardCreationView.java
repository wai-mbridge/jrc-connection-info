package views;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

import controllers.InfoCardCreationController;
import databases.RouteSectionDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.RouteSection;
import utils.AlertUtil;

public class InfoCardCreationView {

    private final Stage stage;
    private final Connection connection;

    public InfoCardCreationView(Stage stage, Connection connection) {
        this.stage = stage;
        this.connection = connection;
    }

    public void show() {
        InputStream imageStream = getClass().getClassLoader().getResourceAsStream("icons/jr.jpg");
        Image icon = new Image(imageStream);
        stage.getIcons().add(icon);

        stage.setTitle("案内カード作成");

        Label title = new Label("最終実施日時");
        title.setMinWidth(140);
        title.setMinHeight(30);
        title.setAlignment(Pos.CENTER);
        title.setStyle("""
                    -fx-background-color: #dbeaf7;
                    -fx-border-color: #9aaec4;
                    -fx-border-width: 1;
                    -fx-font-weight: bold;
                """);

        Label value = new Label(getLastUploadDate());
        value.setMinWidth(220);
        value.setMinHeight(30);
        value.setAlignment(Pos.CENTER_LEFT);
        value.setPadding(new Insets(0, 10, 0, 10));
        value.setStyle("""
                    -fx-background-color: white;
                    -fx-border-color: #9aaec4;
                    -fx-border-width: 1;
                """);

        HBox box = new HBox(title, value);
        box.setSpacing(0);
        box.setPadding(new Insets(10));

        Button execute_btn = new Button("出力");
        execute_btn.setStyle("-fx-text-fill: blue;");
        Button back_btn = new Button("戻る");
        back_btn.setStyle("-fx-text-fill: red;");

        HBox buttonBox = new HBox(10, back_btn, execute_btn);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setCenter(box);
        root.setBottom(buttonBox);

        InfoCardCreationController controller = new InfoCardCreationController(stage, connection);
        execute_btn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("駅設定出力");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv"));
            File file = fileChooser.showSaveDialog(stage);

            if (file == null)
                return;

            boolean success = controller.execute();

            if (success) {
                AlertUtil.showDownloadCompleteDialog(file);
            } else {
                AlertUtil.showAlert("エラー", "データエクスポートが失敗しました!", Alert.AlertType.ERROR);
            }
        });

        back_btn.setOnAction(e -> {
            controller.goBack();
        });

        Scene scene = new Scene(root, 500, 300);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    private String getLastUploadDate() {
        String last_date = "";
        try {
            RouteSectionDAO route_section_dao = new RouteSectionDAO(connection);
            RouteSection route_section = route_section_dao.getLastInsertedRecord();
            if (route_section != null) {
                last_date = route_section.getUploaded_at();
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return last_date;
    }

}
