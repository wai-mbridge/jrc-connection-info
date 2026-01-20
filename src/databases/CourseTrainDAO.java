package databases;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import models.CourseTrain;

public class CourseTrainDAO extends BasicDAO<CourseTrain> {

    public CourseTrainDAO(Connection connection) {
        super(connection);
    }

    @Override
    protected String tableName() {
        return "course_trains";
    }

    @Override
    protected CourseTrain map(ResultSet rs) throws SQLException {
        CourseTrain ct = new CourseTrain();
        ct.setId(rs.getInt("ID"));
        ct.setCourse_id(rs.getInt("course_id"));
        ct.setPosition(rs.getInt("position"));
        ct.setTrain_number(rs.getString("train_number"));
        ct.setCrew_type(rs.getInt("crew_type"));
        ct.setDirect_from(rs.getString("direct_from"));
        ct.setDirect_to(rs.getString("direct_to"));
        return ct;
    }

    @Override
    protected Map<String, Object> toMap(CourseTrain ct) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("course_id", ct.getCourse_id());
        map.put("position", ct.getPosition());
        map.put("train_number", ct.getTrain_number());
        map.put("crew_type", ct.getCrew_type());
        map.put("direct_from", ct.getDirect_from());
        map.put("direct_to", ct.getDirect_to());
        return map;
    }

    /* Optional custom queries */
    public List<CourseTrain> getByCourseId(int course_id) throws SQLException {
        return findBy("course_id", course_id);
    }
}
