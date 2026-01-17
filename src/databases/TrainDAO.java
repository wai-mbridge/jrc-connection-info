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
        t.setID(rs.getInt("ID"));
        t.setRoute_section_id(rs.getInt("route_section_id"));
        t.setTrain_number(rs.getString("train_number"));
        t.setWeekday_holiday_type(rs.getString("weekday_holiday_type"));
        t.setUp_down_type(rs.getString("up_down_type"));
        t.setTrain_type(rs.getString("train_type"));
        t.setStart_station(rs.getString("start_station"));
        t.setEnd_station(rs.getString("end_station"));
        t.setRemark(rs.getString("remark"));
        return t;
    }

    @Override
    protected Map<String, Object> toMap(Train t) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("route_section_id", t.getRoute_section_id());
        map.put("train_number", t.getTrain_number());
        map.put("weekday_holiday_type", t.getWeekday_holiday_type());
        map.put("up_down_type", t.getUp_down_type());
        map.put("train_type", t.getTrain_type());
        map.put("start_station", t.getStart_station());
        map.put("end_station", t.getEnd_station());
        map.put("remark", t.getRemark());
        return map;
    }
}
