package models;

import java.util.List;

public class RouteSection {
    private int ID;
    private int sequence;
    private String name;
    private String short_name;
    private String timetable_format;
    private String timetable_diagram;
    private String timetable_page_layout;
    private String uploaded_at;
    private String timetable_revision_date;
    private String timetable_revision_version;

    private List<Train> trains;

    public RouteSection() {
        super();
    }

    public RouteSection(int ID, String name, String timetable_revision_version, String timetable_revision_date,
            String uploaded_at) {
        super();
        this.ID = ID;
        this.name = name;
        this.timetable_revision_version = timetable_revision_version;
        this.timetable_revision_date = timetable_revision_date;
        this.uploaded_at = uploaded_at;
    }

    public RouteSection(int sequence, String name, String short_name, String timetable_format, String timetable_diagram,
            String timetable_page_layout, String uploaded_at, String timetable_revision_version,
            String timetable_revision_date) {
        super();
        this.sequence = sequence;
        this.name = name;
        this.short_name = short_name;
        this.timetable_format = timetable_format;
        this.timetable_diagram = timetable_diagram;
        this.timetable_page_layout = timetable_page_layout;
        this.uploaded_at = uploaded_at;

        this.timetable_revision_version = timetable_revision_version;
        this.timetable_revision_date = timetable_revision_date;
    }

    public List<Train> getTrains() {
        return trains;
    }

    public void setTrains(List<Train> trains) {
        this.trains = trains;
    }

    public int getID() {
        return ID;
    }

    public void setID(int iD) {
        ID = iD;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    public String getTimetable_format() {
        return timetable_format;
    }

    public void setTimetable_format(String timetable_format) {
        this.timetable_format = timetable_format;
    }

    public String getTimetable_diagram() {
        return timetable_diagram;
    }

    public void setTimetable_diagram(String timetable_diagram) {
        this.timetable_diagram = timetable_diagram;
    }

    public String getTimetable_page_layout() {
        return timetable_page_layout;
    }

    public void setTimetable_page_layout(String timetable_page_layout) {
        this.timetable_page_layout = timetable_page_layout;
    }

    public String getUploaded_at() {
        return uploaded_at;
    }

    public void setUploaded_at(String uploaded_at) {
        this.uploaded_at = uploaded_at;
    }

    public String getTimetable_revision_date() {
        return timetable_revision_date;
    }

    public void setTimetable_revision_date(String timetable_revision_date) {
        this.timetable_revision_date = timetable_revision_date;
    }

    public String getTimetable_revision_version() {
        return timetable_revision_version;
    }

    public void setTimetable_revision_version(String timetable_revision_version) {
        this.timetable_revision_version = timetable_revision_version;
    }

}
