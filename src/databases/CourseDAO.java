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
        Course c = new Course();
        c.setId(rs.getInt("ID"));
        c.setDay_type(rs.getInt("day_type"));
        c.setDay_code(rs.getString("day_code"));
        c.setCourse_number(rs.getString("course_number"));
        c.setIssue(rs.getString("issue"));
        c.setIssue_date(rs.getString("issue_date"));
        c.setUploaded_at(rs.getString("uploaded_at"));
        return c;
    }

    @Override
    protected Map<String, Object> toMap(Course course) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("day_type", course.getDay_type());
        map.put("day_code", course.getDay_code());
        map.put("course_number", course.getCourse_number());
        map.put("issue", course.getIssue());
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
