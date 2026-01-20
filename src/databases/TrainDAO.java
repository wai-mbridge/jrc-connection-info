package databases;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import models.Train;

public class TrainDAO extends BasicDAO<Train> {

    public TrainDAO(Connection connection) {
        super(connection);
    }

    @Override
    protected String tableName() {
        return "trains";
    }

    @Override
    protected Train map(ResultSet rs) throws SQLException {
        Train t = new Train();
        t.setId(rs.getInt("id"));
        t.setRoute_section_id(rs.getInt("route_section_id"));
        t.setTrain_number(rs.getString("train_number"));
        t.setBound_type(rs.getInt("bound_type"));
        t.setDay_type(rs.getInt("day_type"));
        t.setTrain_type(rs.getInt("train_type"));
        t.setFirst_station(rs.getString("first_station"));
        t.setLast_station(rs.getString("last_station"));
        t.setSupplement(rs.getString("supplement"));
        return t;
    }

    @Override
    protected Map<String, Object> toMap(Train t) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("route_section_id", t.getRoute_section_id());
        map.put("train_number", t.getTrain_number());
        map.put("bound_type", t.getBound_type());
        map.put("day_type", t.getDay_type());
        map.put("train_type", t.getTrain_type());
        map.put("first_station", t.getFirst_station());
        map.put("last_station", t.getLast_station());
        map.put("supplement", t.getSupplement());
        return map;
    }

}
