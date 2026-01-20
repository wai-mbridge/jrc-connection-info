package models;

import java.util.List;

public class Course {

    private int id;
    private int day_type;
    private String day_code;
    private String course_number;
    private String issue;
    private String issue_date;
    private String uploaded_at;

    private boolean headerRow = false;
    private String headerText;

    private List<CourseTrain> course_trains;

    public Course() {
        super();
    }

    public Course(String course_number, String issue, String issue_date, String uploaded_at) {
        super();
        this.course_number = course_number;
        this.issue = issue;
        this.issue_date = issue_date;
        this.uploaded_at = uploaded_at;
    }

    public Course(int id, int day_type, String day_code, String course_number, String issue, String issue_date,
            String uploaded_at, boolean headerRow, String headerText, List<CourseTrain> course_trains) {
        super();
        this.id = id;
        this.day_type = day_type;
        this.day_code = day_code;
        this.course_number = course_number;
        this.issue = issue;
        this.issue_date = issue_date;
        this.uploaded_at = uploaded_at;
        this.headerRow = headerRow;
        this.headerText = headerText;
        this.course_trains = course_trains;
    }

    public List<CourseTrain> getCourse_trains() {
        return course_trains;
    }

    public void setCourse_trains(List<CourseTrain> course_trains) {
        this.course_trains = course_trains;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDay_type() {
        return day_type;
    }

    public void setDay_type(int day_type) {
        this.day_type = day_type;
    }

    public String getDay_code() {
        return day_code;
    }

    public void setDay_code(String day_code) {
        this.day_code = day_code;
    }

    public String getCourse_number() {
        return course_number;
    }

    public void setCourse_number(String course_number) {
        this.course_number = course_number;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getIssue_date() {
        return issue_date;
    }

    public void setIssue_date(String issue_date) {
        this.issue_date = issue_date;
    }

    public String getUploaded_at() {
        return uploaded_at;
    }

    public void setUploaded_at(String uploaded_at) {
        this.uploaded_at = uploaded_at;
    }

    public boolean isHeaderRow() {
        return headerRow;
    }

    public void setHeaderRow(boolean headerRow) {
        this.headerRow = headerRow;
    }

    public String getHeaderText() {
        return headerText;
    }

    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }

}
