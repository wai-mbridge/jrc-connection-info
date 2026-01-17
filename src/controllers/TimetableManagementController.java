package controllers;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import databases.RouteSectionDAO;
import javafx.stage.Stage;
import models.RouteSection;
import views.MenuSelectionView;

public class TimetableManagementController {
    private final Stage stage;
    private final Connection connection;

    public TimetableManagementController(Stage stage, Connection connection) {
        this.stage = stage;
        this.connection = connection;
    }

    public List<RouteSection> getAllDatas() {
        List<RouteSection> data = new ArrayList<RouteSection>();
        try {
            RouteSectionDAO dao = new RouteSectionDAO(connection);
            data = dao.all();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public void goBack() {
        new MenuSelectionView(stage, connection).show();
    }

    public void readData() {
// read pdf
    }

}
