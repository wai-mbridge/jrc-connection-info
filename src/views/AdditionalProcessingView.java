package views;

import java.io.InputStream;
import java.sql.Connection;

import controllers.AdditionalProcessingController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.RouteSection;

public class AdditionalProcessingView {

    private final Stage stage;
    private final Connection connection;

    public AdditionalProcessingView(Stage stage, Connection connection) {
        this.stage = stage;
        this.connection = connection;
    }

    public void show() {

        AdditionalProcessingController controller = new AdditionalProcessingController(stage, connection);

        InputStream imageStream = getClass().getClassLoader().getResourceAsStream("icons/jr.jpg");
        Image icon = new Image(imageStream);
        stage.getIcons().add(icon);

        stage.setTitle("補足処理");

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

        RouteSection last = controller.loadLastRecord();
        Label value = new Label(last != null ? last.getUploaded_at() : "");
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

        Button execute_btn = new Button("実行");
        execute_btn.setStyle("-fx-text-fill: blue;");
        Button back_btn = new Button("戻る");
        back_btn.setStyle("-fx-text-fill: red;");

        HBox buttonBox = new HBox(10, back_btn, execute_btn);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setCenter(box);
        root.setBottom(buttonBox);

        execute_btn.setOnAction(e -> {
            controller.execute();
        });

        back_btn.setOnAction(e -> {
            controller.goBack();
        });

        Scene scene = new Scene(root, 500, 300);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

}
