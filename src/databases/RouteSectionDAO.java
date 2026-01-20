package databases;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import models.RouteSection;

public class RouteSectionDAO extends BasicDAO<RouteSection> {

    public RouteSectionDAO(Connection connection) {
        super(connection);
    }

    @Override
    protected String tableName() {
        return "route_sections";
    }

    @Override
    protected RouteSection map(ResultSet rs) throws SQLException {
        RouteSection r = new RouteSection();
        r.setId(rs.getInt("id"));
        r.setPosition(rs.getInt("position"));
        r.setName(rs.getString("name"));
        r.setLetter(rs.getString("letter"));
        r.setTimetable_format_type(rs.getInt("timetable_format_type"));
        r.setTimetable_day_type(rs.getInt("timetable_day_type"));
        r.setTimetable_layout_type(rs.getInt("timetable_layout_type"));
        r.setUploaded_at(rs.getString("uploaded_at"));
        r.setTimetable_version(rs.getString("timetable_version"));
        r.setTimetable_updated(rs.getString("timetable_updated"));
        return r;
    }

    @Override
    protected Map<String, Object> toMap(RouteSection rs) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("position", rs.getPosition());
        map.put("name", rs.getName());
        map.put("letter", rs.getLetter());
        map.put("timetable_format_type", rs.getTimetable_format_type());
        map.put("timetable_day_type", rs.getTimetable_day_type());
        map.put("timetable_layout_type", rs.getTimetable_layout_type());
        map.put("uploaded_at", rs.getUploaded_at());
        map.put("timetable_version", rs.getTimetable_version());
        map.put("timetable_updated", rs.getTimetable_updated());
        return map;
    }

    public RouteSection getWithTrains(int route_section_id) throws SQLException {
        RouteSection section = findById(route_section_id);
        if (section == null)
            return null;

        TrainDAO trainDAO = new TrainDAO(connection);
        section.setTrains(trainDAO.findBy("route_section_id", route_section_id));
        return section;
    }

    public RouteSection getLastInsertedRecord() throws SQLException {
        List<RouteSection> list = query("SELECT * FROM " + tableName() + " ORDER BY uploaded_at DESC LIMIT 1");
        return list.isEmpty() ? null : list.get(0);
    }
}
