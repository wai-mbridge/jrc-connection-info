package databases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

public abstract class BasicDAO<T> {

    protected final Connection connection;

    protected BasicDAO(Connection connection) {
        this.connection = connection;
    }

    protected abstract String tableName();

    // Convert ResultSet row into model object
    protected abstract T map(ResultSet rs) throws SQLException;

    // Convert model object into column-value map
    protected abstract Map<String, Object> toMap(T item);

    public List<T> all() throws SQLException {
        List<T> list = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName();
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public List<T> findAll() throws SQLException {
        return query("SELECT * FROM " + tableName());
    }

    public T findById(long id) throws SQLException {
        return queryOne("SELECT * FROM " + tableName() + " WHERE ID = ?", id);
    }

    public List<T> findBy(String column, Object value) throws SQLException {
        return query("SELECT * FROM " + tableName() + " WHERE " + column + " = ?", value);
    }

    public int insert(T item) throws SQLException {
        Map<String, Object> data = toMap(item); // Convert your object to map of columns and values
        StringJoiner cols = new StringJoiner(",");
        StringJoiner qs = new StringJoiner(",");
        List<Object> params = new ArrayList<>();

        data.forEach((k, v) -> {
            cols.add(k);
            qs.add("?");
            params.add(v);
        });

        String sql = "INSERT INTO " + tableName() + " (" + cols + ") VALUES (" + qs + ")";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Bind parameters
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ps.executeUpdate();

            // Get generated ID
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        return -1;
    }

    public void insertAll(List<T> items) throws SQLException {
        if (items == null || items.isEmpty())
            return;

        Map<String, Object> firstMap = toMap(items.get(0));
        Set<String> columns = firstMap.keySet();

        StringJoiner colNames = new StringJoiner(",");
        StringJoiner placeholders = new StringJoiner(",");
        columns.forEach(col -> {
            colNames.add(col);
            placeholders.add("?");
        });

        String sql = "INSERT INTO " + tableName() + " (" + colNames + ") VALUES (" + placeholders + ")";

        boolean autoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (T item : items) {
                Map<String, Object> map = toMap(item);
                int i = 1;
                for (String col : columns) {
                    ps.setObject(i++, map.get(col));
                }
                ps.addBatch();
            }
            ps.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(autoCommit);
        }
    }

    public void update(long id, Map<String, Object> data) throws SQLException {
        StringJoiner set = new StringJoiner(",");
        List<Object> params = new ArrayList<>();

        data.forEach((k, v) -> {
            set.add(k + "=?");
            params.add(v);
        });
        params.add(id);

        String sql = "UPDATE " + tableName() + " SET " + set + " WHERE ID=?";
        execute(sql, params);
    }

    public void delete(long id) throws SQLException {
        execute("DELETE FROM " + tableName() + " WHERE ID=?", List.of(id));
    }

    public void deleteBy(String column, Object value) throws SQLException {
        String sql = "DELETE FROM " + tableName() + " WHERE " + column + " = ?";
        execute(sql, List.of(value));
    }

    protected List<T> query(String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            bind(ps, params);
            ResultSet rs = ps.executeQuery();
            List<T> list = new ArrayList<>();
            while (rs.next())
                list.add(map(rs));
            return list;
        }
    }

    protected T queryOne(String sql, Object... params) throws SQLException {
        List<T> list = query(sql, params);
        return list.isEmpty() ? null : list.get(0);
    }

    protected void execute(String sql, List<Object> params) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            bind(ps, params.toArray());
            ps.executeUpdate();
        }
    }

    private void bind(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }

}
