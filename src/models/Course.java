package models;

import java.util.List;

public class Course {
    private int ID;
    private String weekday_holiday_type;
    private String route_day_of_week;
    private String route_number;
    private String plan_name;
    private String issue_date;
    private String uploaded_at;

    private boolean headerRow = false;
    private String headerText;

    private List<CourseTrain> course_trains;

    public Course() {
        super();
    }

    public Course(String route_number, String plan_name, String issue_date, String uploaded_at) {
        super();
        this.route_number = route_number;
        this.plan_name = plan_name;
        this.issue_date = issue_date;
        this.uploaded_at = uploaded_at;
    }

    public Course(int ID, String weekday_holiday_type, String route_day_of_week, String route_number, String plan_name,
            String issue_date, String uploaded_at) {
        super();
        this.ID = ID;
        this.weekday_holiday_type = weekday_holiday_type;
        this.route_day_of_week = route_day_of_week;
        this.route_number = route_number;
        this.plan_name = plan_name;
        this.issue_date = issue_date;
        this.uploaded_at = uploaded_at;
    }

    public List<CourseTrain> getCourse_trains() {
        return course_trains;
    }

    public void setCourse_trains(List<CourseTrain> course_trains) {
        this.course_trains = course_trains;
    }

    public int getID() {
        return ID;
    }

    public void setID(int iD) {
        ID = iD;
    }

    public String getWeekday_holiday_type() {
        return weekday_holiday_type;
    }

    public void setWeekday_holiday_type(String weekday_holiday_type) {
        this.weekday_holiday_type = weekday_holiday_type;
    }

    public String getRoute_day_of_week() {
        return route_day_of_week;
    }

    public void setRoute_day_of_week(String route_day_of_week) {
        this.route_day_of_week = route_day_of_week;
    }

    public String getRoute_number() {
        return route_number;
    }

    public void setRoute_number(String route_number) {
        this.route_number = route_number;
    }

    public String getPlan_name() {
        return plan_name;
    }

    public void setPlan_name(String plan_name) {
        this.plan_name = plan_name;
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
