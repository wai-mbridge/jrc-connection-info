package views;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import controllers.StationManagementController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
import models.StationSetting;
import utils.AlertUtil;

public class StationManagementView {

    private final Stage stage;
    private final Connection connection;
    private TableView<StationSetting> tableView = new TableView<>();

    public StationManagementView(Stage stage, Connection connection) {
        this.stage = stage;
        this.connection = connection;
    }

    public void show() {
        InputStream imageStream = getClass().getClassLoader().getResourceAsStream("icons/jr.jpg");
        Image icon = new Image(imageStream);
        stage.getIcons().add(icon);

        stage.setTitle("駅設定");
        tableView.setPlaceholder(new Label("データがありません"));

        TableColumn<StationSetting, String> station_name_col = new TableColumn<>("駅名");
        station_name_col.setCellValueFactory(new PropertyValueFactory<>("station_name"));
        station_name_col.setPrefWidth(220);

        TableColumn<StationSetting, String> connection_grace_time_col = new TableColumn<>("接続猶予時間(秒)");
        connection_grace_time_col.setCellValueFactory(new PropertyValueFactory<>("connection_grace_time"));
        connection_grace_time_col.setPrefWidth(140);

        TableColumn<StationSetting, String> connection_extract_count_col = new TableColumn<>("接続抽出数");
        connection_extract_count_col.setCellValueFactory(new PropertyValueFactory<>("connection_extract_count"));
        connection_extract_count_col.setPrefWidth(140);

        tableView.getColumns()
                .addAll(List.of(station_name_col, connection_grace_time_col, connection_extract_count_col));

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        Button import_btn = new Button("取込");
        import_btn.setStyle("-fx-text-fill: blue;");
        Button export_btn = new Button("出力");
        export_btn.setStyle("-fx-text-fill: blue;");
        Button back_btn = new Button("戻る");
        back_btn.setStyle("-fx-text-fill: red;");

        HBox buttonBox = new HBox(10, back_btn, export_btn, import_btn);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setCenter(tableView);
        root.setBottom(buttonBox);

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);

        StackPane stackPane = new StackPane(root, progressIndicator);
        StackPane.setAlignment(progressIndicator, Pos.CENTER);

        StationManagementController controller = new StationManagementController(stage, connection);

        import_btn.setOnAction(e -> {

            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv"));
            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                import_btn.setDisable(true);
                back_btn.setDisable(true);
                progressIndicator.setVisible(true);
                stage.getScene().setCursor(javafx.scene.Cursor.WAIT);

                new Thread(() -> {
                    try {
                        controller.readData(selectedFile);
                        AlertUtil.showAlert("情報", "データベースに保存しました!", Alert.AlertType.INFORMATION);

                    } catch (SQLException sql_ex) {
                        // sqlerror
                    } catch (IOException io_ex) {
                        // io error
                    } finally {
                        Platform.runLater(() -> {
                            progressIndicator.setVisible(false);
                            import_btn.setDisable(false);
                            back_btn.setDisable(false);
                            stage.getScene().setCursor(javafx.scene.Cursor.DEFAULT);

                            // refresh table
                            setData(controller.getAllDatas());
                        });
                    }
                }).start();

            }
        });

        export_btn.setOnAction(e -> {

            List<StationSetting> station_setting_list = controller.getAllDatas();
            if (station_setting_list.isEmpty()) {
                AlertUtil.showAlert("エラー", "エクスポートするデータがありません!", Alert.AlertType.ERROR);
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("駅設定出力");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv"));
            File file = fileChooser.showSaveDialog(stage);

            if (file == null)
                return;

            boolean success = controller.exportData(file, station_setting_list);

            if (success) {
                AlertUtil.showDownloadCompleteDialog(file);
            } else {
                AlertUtil.showAlert("エラー", "データエクスポートが失敗しました!", Alert.AlertType.ERROR);
            }

        });

        back_btn.setOnAction(e -> {
            controller.goBack();
        });

        Scene scene = new Scene(stackPane, 500, 300);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();

        setData(controller.getAllDatas());

    }

    public void setData(List<StationSetting> data) {
        tableView.setItems(FXCollections.observableArrayList(data));
    }
}
