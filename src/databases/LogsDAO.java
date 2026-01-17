package databases;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import models.Logs;

public class LogsDAO extends BasicDAO<Logs> {

    public LogsDAO(Connection connection) {
        super(connection);
    }

    @Override
    protected String tableName() {
        return "logs";
    }

    @Override
    protected Logs map(ResultSet rs) throws SQLException {
        Logs l = new Logs();
        l.setID(rs.getInt("ID"));
        l.setAction_type(rs.getString("action_type"));
        l.setExecuted_at(rs.getString("executed_at"));
        return l;
    }

    @Override
    protected Map<String, Object> toMap(Logs log) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("action_type", log.getAction_type());
        map.put("executed_at", log.getExecuted_at());
        return map;
    }
}
