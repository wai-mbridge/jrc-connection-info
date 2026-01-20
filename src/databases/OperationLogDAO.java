package databases;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import models.OperationLog;

public class OperationLogDAO extends BasicDAO<OperationLog> {

    public OperationLogDAO(Connection connection) {
        super(connection);
    }

    @Override
    protected String tableName() {
        return "operation_logs";
    }

    @Override
    protected OperationLog map(ResultSet rs) throws SQLException {
        OperationLog l = new OperationLog();
        l.setId(rs.getInt("id"));
        l.setOperation_type(rs.getInt("operation_type"));
        l.setOperated_at(rs.getString("operated_at"));
        return l;
    }

    @Override
    protected Map<String, Object> toMap(OperationLog log) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("operation_type", log.getOperation_type());
        map.put("operated_at", log.getOperated_at());
        return map;
    }

}
