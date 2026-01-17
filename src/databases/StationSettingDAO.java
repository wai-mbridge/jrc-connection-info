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
        s.setID(rs.getInt("ID"));
        s.setStation_name(rs.getString("station_name"));
        s.setConnection_grace_time(rs.getInt("connection_grace_time"));
        s.setConnection_extract_count(rs.getInt("connection_extract_count"));
        return s;
    }

    @Override
    protected Map<String, Object> toMap(StationSetting s) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("station_name", s.getStation_name());
        map.put("connection_grace_time", s.getConnection_grace_time());
        map.put("connection_extract_count", s.getConnection_extract_count());
        return map;
    }

    public StationSetting getLastInsertedRecord() throws SQLException {
        String sql = "SELECT * FROM " + tableName() + " ORDER BY ID DESC LIMIT 1";
        List<StationSetting> list = query(sql);
        return list.isEmpty() ? null : list.get(0);
    }
}
