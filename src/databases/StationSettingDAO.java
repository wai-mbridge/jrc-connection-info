package databases;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import models.StationSetting;

public class StationSettingDAO extends BasicDAO<StationSetting> {

    public StationSettingDAO(Connection connection) {
        super(connection);
    }

    @Override
    protected String tableName() {
        return "station_settings";
    }

    @Override
    protected StationSetting map(ResultSet rs) throws SQLException {
        StationSetting s = new StationSetting();
        s.setId(rs.getInt("id"));
        s.setStation_name(rs.getString("station_name"));
        s.setDuration(rs.getInt("duration"));
        s.setExtraction(rs.getInt("extraction"));
        return s;
    }

    @Override
    protected Map<String, Object> toMap(StationSetting s) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("station_name", s.getStation_name());
        map.put("duration", s.getDuration());
        map.put("extraction", s.getExtraction());
        return map;
    }

    public StationSetting getLastInsertedRecord() throws SQLException {
        String sql = "SELECT * FROM " + tableName() + " ORDER BY ID DESC LIMIT 1";
        List<StationSetting> list = query(sql);
        return list.isEmpty() ? null : list.get(0);
    }
}
