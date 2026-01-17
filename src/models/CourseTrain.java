package models;

public class CourseTrain {

    private int ID;
    private int course_id;
    private String train_number;
    private int sequence;
    private String crew_type;
    private String direct_source_train_number;
    private String direct_destination__train_number;

    private Course course;

    public CourseTrain() {
        super();
    }

    public CourseTrain(int iD, int course_id, String train_number, int sequence, String crew_type,
            String direct_source_train_number, String direct_destination__train_number) {
        super();
        ID = iD;
        this.course_id = course_id;
        this.train_number = train_number;
        this.sequence = sequence;
        this.crew_type = crew_type;
        this.direct_source_train_number = direct_source_train_number;
        this.direct_destination__train_number = direct_destination__train_number;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public int getID() {
        return ID;
    }

    public void setID(int iD) {
        ID = iD;
    }

    public int getCourse_id() {
        return course_id;
    }

    public void setCourse_id(int course_id) {
        this.course_id = course_id;
    }

    public String getTrain_number() {
        return train_number;
    }

    public void setTrain_number(String train_number) {
        this.train_number = train_number;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getCrew_type() {
        return crew_type;
    }

    public void setCrew_type(String crew_type) {
        this.crew_type = crew_type;
    }

    public String getDirect_source_train_number() {
        return direct_source_train_number;
    }

    public void setDirect_source_train_number(String direct_source_train_number) {
        this.direct_source_train_number = direct_source_train_number;
    }

    public String getDirect_destination__train_number() {
        return direct_destination__train_number;
    }

    public void setDirect_destination__train_number(String direct_destination__train_number) {
        this.direct_destination__train_number = direct_destination__train_number;
    }

}
