package views;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.util.List;

import controllers.TimetableManagementController;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.RouteSection;

public class TimetableManagementView {

    private final Stage stage;
    private final Connection connection;
    private TableView<RouteSection> tableView = new TableView<>();

    public TimetableManagementView(Stage stage, Connection connection) {
        this.stage = stage;
        this.connection = connection;
    }

    public void show() {
        InputStream imageStream = getClass().getClassLoader().getResourceAsStream("icons/jr.jpg");
        Image icon = new Image(imageStream);
        stage.getIcons().add(icon);

        stage.setTitle("時刻表管理");
        tableView.setPlaceholder(new Label("データがありません"));

        TableColumn<RouteSection, String> name_col = new TableColumn<>("線区名");
        name_col.setCellValueFactory(new PropertyValueFactory<>("name"));
        name_col.setPrefWidth(200);

        TableColumn<RouteSection, String> timetable_revision_date_col = new TableColumn<>("改正日");
        timetable_revision_date_col.setCellValueFactory(new PropertyValueFactory<>("timetable_revision_date"));
        timetable_revision_date_col.setPrefWidth(100);

        TableColumn<RouteSection, String> timetable_revision_version_col = new TableColumn<>("改正版");
        timetable_revision_version_col.setCellValueFactory(new PropertyValueFactory<>("timetable_revision_version"));
        timetable_revision_version_col.setPrefWidth(50);

        TableColumn<RouteSection, String> uploaded_at_col = new TableColumn<>("アップロード日");
        uploaded_at_col.setCellValueFactory(new PropertyValueFactory<>("uploaded_at"));
        uploaded_at_col.setPrefWidth(150);

        tableView.getColumns().addAll(
                List.of(name_col, timetable_revision_date_col, timetable_revision_version_col, uploaded_at_col));

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        Button import_btn = new Button("取込");
        import_btn.setStyle("-fx-text-fill: blue;");
        Button back_btn = new Button("戻る");
        back_btn.setStyle("-fx-text-fill: red;");

        HBox buttonBox = new HBox(10, back_btn, import_btn);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setCenter(tableView);
        root.setBottom(buttonBox);

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);

        StackPane stackPane = new StackPane(root, progressIndicator);
        StackPane.setAlignment(progressIndicator, Pos.CENTER);

        TimetableManagementController controller = new TimetableManagementController(stage, connection);

        import_btn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf"));
            List<File> selectedFiles = fileChooser.showOpenMultipleDialog(stage);
            if (selectedFiles != null && !selectedFiles.isEmpty()) {
                for (File file : selectedFiles) {
                    controller.readData();
                }

                // refresh
                setData(controller.getAllDatas());
            }

        });

        back_btn.setOnAction(e -> {
            controller.goBack();
        });

        setData(controller.getAllDatas());

        Scene scene = new Scene(stackPane, 500, 300);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();

    }

    public void setData(List<RouteSection> data) {
        tableView.setItems(FXCollections.observableArrayList(data));
    }
}
