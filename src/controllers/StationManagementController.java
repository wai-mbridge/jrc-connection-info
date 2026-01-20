package controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import databases.StationSettingDAO;
import javafx.stage.Stage;
import models.StationSetting;
import views.MenuSelectionView;

public class StationManagementController {

    private final Stage stage;
    private final Connection connection;

    public StationManagementController(Stage stage, Connection connection) {
        this.stage = stage;
        this.connection = connection;
    }

    public void readData(File file) throws SQLException, IOException {
        List<StationSetting> station_settings = new ArrayList<StationSetting>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        boolean first_line = true;
        while ((line = br.readLine()) != null) {
            if (first_line) {
                first_line = false;
                continue;
            }

            String[] tokens = line.split(",");
            if (tokens.length >= 3) {
                StationSetting s = new StationSetting(tokens[0].trim(), Integer.parseInt(tokens[1].trim()),
                        Integer.parseInt(tokens[2].trim()));
                station_settings.add(s);
            }
        }
        br.close();

        if (station_settings != null) {
            StationSettingDAO station_setting_dao = new StationSettingDAO(connection);
            station_setting_dao.insertAll(station_settings);
        }
    }

    public boolean exportData(File file, List<StationSetting> station_setting_list) {

        boolean success = false;
        try (FileWriter writer = new FileWriter(file)) {
            // Write header
            writer.append("駅名,接続猶予時間(秒),接続抽出数\n");

            // Write data
            for (StationSetting station_setting : station_setting_list) {
                writer.append(station_setting.getStation_name() + ",").append(station_setting.getDuration() + ",")
                        .append(station_setting.getExtraction() + "\n");
            }

            writer.flush();
            success = true;

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return success;
    }

    public List<StationSetting> getAllDatas() {
        List<StationSetting> data = new ArrayList<StationSetting>();
        try {
            StationSettingDAO dao = new StationSettingDAO(connection);
            data = dao.all();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public void goBack() {
        new MenuSelectionView(stage, connection).show();
    }

}
