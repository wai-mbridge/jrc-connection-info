package databases;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import models.Course;

public class CourseDAO extends BasicDAO<Course> {

    public CourseDAO(Connection connection) {
        super(connection);
    }

    @Override
    protected String tableName() {
        return "courses";
    }

    @Override
    protected Course map(ResultSet rs) throws SQLException {
        return new Course(rs.getInt("ID"), rs.getString("weekday_holiday_type"), rs.getString("route_day_of_week"),
                rs.getString("route_number"), rs.getString("plan_name"), rs.getString("issue_date"),
                rs.getString("uploaded_at"));
    }

    @Override
    protected Map<String, Object> toMap(Course course) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("weekday_holiday_type", course.getWeekday_holiday_type());
        map.put("route_day_of_week", course.getRoute_day_of_week());
        map.put("route_number", course.getRoute_number());
        map.put("plan_name", course.getPlan_name());
        map.put("issue_date", course.getIssue_date());
        map.put("uploaded_at", course.getUploaded_at());
        return map;
    }

    public List<Course> getAllWeekday() throws SQLException {
        return findBy("weekday_holiday_type", "平日");
    }

    public List<Course> getAllHoliday() throws SQLException {
        return findBy("weekday_holiday_type", "休日");
    }

}
