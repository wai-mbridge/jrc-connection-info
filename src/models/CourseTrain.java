package models;

public class CourseTrain {

    private int id;
    private int course_id;
    private int position;
    private String train_number;
    private int crew_type;
    private String direct_from;
    private String direct_to;

    private Course course;

    public CourseTrain() {
        super();
    }

    public CourseTrain(int id, int course_id, int position, String train_number, int crew_type, String direct_from,
            String direct_to, Course course) {
        super();
        this.id = id;
        this.course_id = course_id;
        this.position = position;
        this.train_number = train_number;
        this.crew_type = crew_type;
        this.direct_from = direct_from;
        this.direct_to = direct_to;
        this.course = course;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCourse_id() {
        return course_id;
    }

    public void setCourse_id(int course_id) {
        this.course_id = course_id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getTrain_number() {
        return train_number;
    }

    public void setTrain_number(String train_number) {
        this.train_number = train_number;
    }

    public int getCrew_type() {
        return crew_type;
    }

    public void setCrew_type(int crew_type) {
        this.crew_type = crew_type;
    }

    public String getDirect_from() {
        return direct_from;
    }

    public void setDirect_from(String direct_from) {
        this.direct_from = direct_from;
    }

    public String getDirect_to() {
        return direct_to;
    }

    public void setDirect_to(String direct_to) {
        this.direct_to = direct_to;
    }

}
