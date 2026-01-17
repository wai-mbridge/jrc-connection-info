package app;

import java.sql.Connection;

import databases.SQLiteService;
import javafx.application.Application;
import javafx.stage.Stage;
import views.MenuSelectionView;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // DB connection
        SQLiteService db_service = new SQLiteService();
        Connection connection = db_service.openConnection();

        stage.setOnCloseRequest(e -> {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (Exception ignored) {
            }
        });

        new MenuSelectionView(stage, connection).show();
        ;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
