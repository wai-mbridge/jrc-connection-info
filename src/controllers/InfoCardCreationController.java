package controllers;

import java.sql.Connection;

import javafx.stage.Stage;
import views.MenuSelectionView;

public class InfoCardCreationController {

    private final Stage stage;
    private final Connection connection;

    public InfoCardCreationController(Stage stage, Connection connection) {
        this.stage = stage;
        this.connection = connection;
    }

    public boolean execute() {
        return true;

    }

    public void goBack() {
        new MenuSelectionView(stage, connection).show();
    }
}
