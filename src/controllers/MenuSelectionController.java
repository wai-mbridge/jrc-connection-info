package controllers;

import java.sql.Connection;

import javafx.stage.Stage;
import views.AdditionalProcessingView;
import views.CourseManagementView;
import views.InfoCardCreationView;
import views.StationManagementView;
import views.TimetableManagementView;

public class MenuSelectionController {

    private final Stage stage;
    private final Connection connection;

    public MenuSelectionController(Stage stage, Connection connection) {
        this.stage = stage;
        this.connection = connection;
    }

    public void openTimetableManagement() {
        new TimetableManagementView(stage, connection).show();
    }

    public void openAdditionalProcessing() {
        new AdditionalProcessingView(stage, connection).show();
    }

    public void openStationManagement() {
        new StationManagementView(stage, connection).show();
    }

    public void openCourseManagement() {
        new CourseManagementView(stage, connection).show();
    }

    public void openInfoCardCreation() {
        new InfoCardCreationView(stage, connection).show();
    }
}
