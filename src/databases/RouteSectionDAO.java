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
        r.setID(rs.getInt("ID"));
        r.setSequence(rs.getInt("sequence"));
        r.setName(rs.getString("name"));
        r.setShort_name(rs.getString("short_name"));
        r.setTimetable_format(rs.getString("timetable_format"));
        r.setTimetable_diagram(rs.getString("timetable_diagram"));
        r.setTimetable_page_layout(rs.getString("timetable_page_layout"));
        r.setUploaded_at(rs.getString("uploaded_at"));
        r.setTimetable_revision_version(rs.getString("timetable_revision_version"));
        r.setTimetable_revision_date(rs.getString("timetable_revision_date"));
        return r;
    }

    @Override
    protected Map<String, Object> toMap(RouteSection rs) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("sequence", rs.getSequence());
        map.put("name", rs.getName());
        map.put("short_name", rs.getShort_name());
        map.put("timetable_format", rs.getTimetable_format());
        map.put("timetable_diagram", rs.getTimetable_diagram());
        map.put("timetable_page_layout", rs.getTimetable_page_layout());
        map.put("timetable_revision_version", rs.getTimetable_revision_version());
        map.put("timetable_revision_date", rs.getTimetable_revision_date());
        map.put("uploaded_at", rs.getUploaded_at());
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
