package databases;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import models.TrainStation;

public class TrainStationDAO extends BasicDAO<TrainStation> {

    public TrainStationDAO(Connection connection) {
        super(connection);
    }

    @Override
    protected String tableName() {
        return "train_stations";
    }

    @Override
    protected TrainStation map(java.sql.ResultSet rs) throws SQLException {
        TrainStation ts = new TrainStation();
        ts.setID(rs.getInt("ID"));
        ts.setTrain_id(rs.getInt("train_id"));
        ts.setStation_number(rs.getString("station_number"));
        ts.setStation_name(rs.getString("station_name"));
        ts.setArrival_time(rs.getString("arrival_time"));
        ts.setDeparture_time(rs.getString("departure_time"));
        ts.setPlatform(rs.getString("platform"));
        ts.setStop_type(rs.getString("stop_type"));
        return ts;
    }

    @Override
    protected Map<String, Object> toMap(TrainStation ts) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("train_id", ts.getTrain_id());
        map.put("station_number", ts.getStation_number());
        map.put("station_name", ts.getStation_name());
        map.put("arrival_time", ts.getArrival_time());
        map.put("departure_time", ts.getDeparture_time());
        map.put("platform", ts.getPlatform());
        map.put("stop_type", ts.getStop_type());
        return map;
    }
}
