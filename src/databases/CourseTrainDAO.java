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
        ct.setID(rs.getInt("ID"));
        ct.setCourse_id(rs.getInt("course_id"));
        ct.setTrain_number(rs.getString("train_number"));
        ct.setSequence(rs.getInt("sequence"));
        ct.setCrew_type(rs.getString("crew_type"));
        ct.setDirect_source_train_number(rs.getString("direct_source_train_number"));
        ct.setDirect_destination__train_number(rs.getString("direct_destination__train_number"));
        return ct;
    }

    @Override
    protected Map<String, Object> toMap(CourseTrain ct) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("course_id", ct.getCourse_id());
        map.put("train_number", ct.getTrain_number());
        map.put("sequence", ct.getSequence());
        map.put("crew_type", ct.getCrew_type());
        map.put("direct_source_train_number", ct.getDirect_source_train_number());
        map.put("direct_destination__train_number", ct.getDirect_destination__train_number());
        return map;
    }

    /* Optional custom queries */
    public List<CourseTrain> getByCourseId(int courseId) throws SQLException {
        return findBy("course_id", courseId);
    }
}
