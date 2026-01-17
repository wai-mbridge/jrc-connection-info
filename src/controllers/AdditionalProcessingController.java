package controllers;

import java.sql.Connection;

import databases.RouteSectionDAO;
import javafx.stage.Stage;
import models.RouteSection;
import views.MenuSelectionView;

public class AdditionalProcessingController {
    private final Stage stage;
    private final Connection connection;

    public AdditionalProcessingController(Stage stage, Connection connection) {
        this.stage = stage;
        this.connection = connection;
    }

    public RouteSection loadLastRecord() {
        RouteSection last = null;
        try {
            RouteSectionDAO dao = new RouteSectionDAO(connection);
            last = dao.getLastInsertedRecord();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return last;
    }

    public void execute() {
        System.out.println("Execute pressed");
    }

    public void goBack() {
        new MenuSelectionView(stage, connection).show();
    }
}
