package views;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

import controllers.CourseManagementController;
import databases.CourseDAO;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Course;
import utils.AlertUtil;

public class CourseManagementView {

    private final Stage stage;
    private final Connection connection;

    public CourseManagementView(Stage stage, Connection connection) {
        this.stage = stage;
        this.connection = connection;
    }

    public void show() {
        InputStream imageStream = getClass().getClassLoader().getResourceAsStream("icons/jr.jpg");
        Image icon = new Image(imageStream);
        stage.getIcons().add(icon);

        stage.setTitle("行路管理");
        TableView<Course> tableView = new TableView<>();
        tableView.setPlaceholder(new Label("データがありません"));

        TableColumn<Course, String> course_number_col = new TableColumn<>("行路番号");
        course_number_col.setCellValueFactory(new PropertyValueFactory<>("course_number"));
        course_number_col.setPrefWidth(70);
        course_number_col.setCellValueFactory(cellData -> {

            Course course = cellData.getValue();
            String course_number = course.getCourse_number();
            if (course.getDay_code() != null) {
                switch (course.getDay_code()) {
                case "01":
                    course_number = "平" + course_number;
                    break;
                case "11":
                    course_number = "平平" + course_number;
                    break;
                case "04":
                    course_number = "休" + course_number;
                    break;
                case "44":
                    course_number = "休休" + course_number;
                    break;
                }
            }

            return new ReadOnlyStringWrapper(course_number);
        });

        TableColumn<Course, String> issue_col = new TableColumn<>("案名称");
        issue_col.setCellValueFactory(new PropertyValueFactory<>("issue"));
        issue_col.setPrefWidth(150);

        TableColumn<Course, String> issue_date_col = new TableColumn<>("発行日");
        issue_date_col.setCellValueFactory(new PropertyValueFactory<>("issue_date"));
        issue_date_col.setPrefWidth(90);

        TableColumn<Course, String> uploaded_at_col = new TableColumn<>("アップロード日");
        uploaded_at_col.setCellValueFactory(new PropertyValueFactory<>("uploaded_at"));
        uploaded_at_col.setPrefWidth(140);

        tableView.getColumns().addAll(List.of(course_number_col, issue_col, issue_date_col, uploaded_at_col));

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        course_number_col.setCellFactory(col -> new TableCell<Course, String>() {
            private final Text text = new Text();

            {
                text.wrappingWidthProperty().bind(col.widthProperty().subtract(12));
                setGraphic(text);
                setPrefHeight(Control.USE_COMPUTED_SIZE);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    setText(null);
                    setStyle("");
                    return;
                }

                Course row = getTableRow().getItem();
                if (row != null && row.isHeaderRow()) {
                    setGraphic(null);
                    setText(row.getHeaderText());
                    setAlignment(Pos.CENTER_LEFT);
                    setStyle("""
                                -fx-background-color: #FCE5CD;
                                -fx-border-width: 0;
                                -fx-background-insets: 0;
                            """);
                    setFont(Font.font("Yu Gothic", FontWeight.BOLD, 12));
                } else {
                    text.setText(item);
                    setGraphic(text);
                    setText(null);
                    setAlignment(Pos.CENTER_LEFT);
                    setStyle("");
                    setFont(Font.font("Yu Gothic", FontWeight.NORMAL, 12));
                }
            }
        });
        issue_col.setCellFactory(col -> new TableCell<Course, String>() {
            private final Text text = new Text();

            {
                text.wrappingWidthProperty().bind(col.widthProperty().subtract(12));
                setGraphic(text);
                setPrefHeight(Control.USE_COMPUTED_SIZE);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                Course row = getTableRow().getItem();
                if (row != null && row.isHeaderRow()) {
                    setGraphic(null);
                    setText(null);
                    setStyle("""
                                -fx-background-color: #FCE5CD;
                                -fx-border-width: 0;
                                -fx-background-insets: 0;
                            """);
                } else {
                    text.setText(item);
                    setGraphic(text);
                    setText(null);
                    setStyle("");
                }
            }
        });

        issue_date_col.setCellFactory(col -> new TableCell<Course, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                Course row = getTableRow().getItem();
                if (row != null && row.isHeaderRow()) {
                    setText(null);
                    setStyle("""
                                -fx-background-color: #FCE5CD;
                                -fx-border-width: 0;
                                -fx-background-insets: 0;
                            """);
                } else {
                    setText(item);
                    setStyle("");
                }
            }
        });

        uploaded_at_col.setCellFactory(col -> new TableCell<Course, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                Course row = getTableRow().getItem();
                if (row != null && row.isHeaderRow()) {
                    setText(null);
                    setStyle("""
                                -fx-background-color: #FCE5CD;
                                -fx-border-width: 0;
                                -fx-background-insets: 0;
                            """);
                } else {
                    setText(item);
                    setStyle("");
                }
            }
        });
        tableView.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Course item, boolean empty) {
                super.updateItem(item, empty);

                if (item != null && item.isHeaderRow()) {
                    setDisable(true);
                } else {
                    setStyle("");
                    setDisable(false);
                }
            }
        });

        for (TableColumn<Course, ?> col : tableView.getColumns()) {
            col.setComparator((o1, o2) -> {
                // This comparator is only used for real data rows
                return ((Comparable) o1).compareTo(o2);
            });
        }

        tableView.sortPolicyProperty().set(tv -> {
            Comparator<Course> base = tv.getComparator();
            if (base == null)
                return true;

            FXCollections.sort(tv.getItems(), (a, b) -> {
                if (a.isHeaderRow() || b.isHeaderRow()) {
                    return 0; // keep header rows in place
                }
                return base.compare(a, b);
            });
            return true;
        });

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

        CourseManagementController controller = new CourseManagementController(stage, connection);

        import_btn.setOnAction(e -> {

            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf"));
            List<File> selectedFiles = fileChooser.showOpenMultipleDialog(stage);
            if (selectedFiles != null && !selectedFiles.isEmpty()) {
                import_btn.setDisable(true);
                back_btn.setDisable(true);
                progressIndicator.setVisible(true);
                stage.getScene().setCursor(javafx.scene.Cursor.WAIT);

                new Thread(() -> {
                    try {
                        for (File file : selectedFiles) {
                            controller.extract(file);
                        }
                        AlertUtil.showAlert("情報", "データベースに保存しました。", Alert.AlertType.INFORMATION);
                    } catch (IOException | SQLException e1) {
                        e1.printStackTrace();
                    } finally {
                        Platform.runLater(() -> {
                            progressIndicator.setVisible(false);
                            import_btn.setDisable(false);
                            back_btn.setDisable(false);
                            stage.getScene().setCursor(javafx.scene.Cursor.DEFAULT);

                            // refresh table
                            tableView.setItems(getAllData());
                        });
                    }
                }).start();
            }
        });

        // DirectoryChooser directoryChooser = new DirectoryChooser();
        // directoryChooser.setTitle("Select Folder");
        //
        // File selectedFolder = directoryChooser.showDialog(stage);
        // if (selectedFolder != null) {
        // // Get all files in the folder
        // File[] files = selectedFolder.listFiles();
        //
        // if (files != null) {
        // for (File file : files) {
        // if (file.isFile()) { // only files, skip subdirectories
        // System.out.println(file.getAbsolutePath());
        // }
        // }
        // }
        // }

        back_btn.setOnAction(e -> {
            controller.goBack();
        });

        Scene scene = new Scene(stackPane, 500, 300);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();

        // Load data from database
        tableView.setItems(getAllData());
        Platform.runLater(tableView::requestLayout);
    }

    private ObservableList<Course> getAllData() {
        ObservableList<Course> tableData = FXCollections.observableArrayList();
        try {

            CourseDAO course_dao = new CourseDAO(connection);
            List<Course> week_day_courses = course_dao.getAllWeekday();
            List<Course> holiday_courses = course_dao.getAllHoliday();
            if (week_day_courses.isEmpty() && holiday_courses.isEmpty()) {
                return null;
            }

            // 平日ヘッダー
            Course weekdayHeader = new Course();
            weekdayHeader.setHeaderRow(true);
            weekdayHeader.setHeaderText("平日");
            tableData.add(weekdayHeader);

            tableData.addAll(week_day_courses);

            // 休日ヘッダー
            Course holidayHeader = new Course();
            holidayHeader.setHeaderRow(true);
            holidayHeader.setHeaderText("休日");
            tableData.add(holidayHeader);

            tableData.addAll(holiday_courses);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tableData;
    }
}
