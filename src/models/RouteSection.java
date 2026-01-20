package models;

import java.util.List;

public class RouteSection {

    private int id;
    private int position;
    private String name;
    private String letter;
    private int timetable_format_type;
    private int timetable_day_type;
    private int timetable_layout_type;
    private String uploaded_at;
    private String timetable_version;
    private String timetable_updated;

    private List<Train> trains;

    public RouteSection() {
        super();
    }

    public RouteSection(int id, String name, String timetable_version, String timetable_updated, String uploaded_at) {
        super();
        this.id = id;
        this.name = name;
        this.timetable_version = timetable_version;
        this.timetable_updated = timetable_updated;
        this.uploaded_at = uploaded_at;
    }

    public RouteSection(int position, String name, String letter, int timetable_format_type, int timetable_day_type,
            int timetable_layout_type, String uploaded_at, String timetable_version, String timetable_updated,
            List<Train> trains) {
        super();
        this.position = position;
        this.name = name;
        this.letter = letter;
        this.timetable_format_type = timetable_format_type;
        this.timetable_day_type = timetable_day_type;
        this.timetable_layout_type = timetable_layout_type;
        this.uploaded_at = uploaded_at;
        this.timetable_version = timetable_version;
        this.timetable_updated = timetable_updated;
        this.trains = trains;
    }

    public List<Train> getTrains() {
        return trains;
    }

    public void setTrains(List<Train> trains) {
        this.trains = trains;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public int getTimetable_format_type() {
        return timetable_format_type;
    }

    public void setTimetable_format_type(int timetable_format_type) {
        this.timetable_format_type = timetable_format_type;
    }

    public int getTimetable_day_type() {
        return timetable_day_type;
    }

    public void setTimetable_day_type(int timetable_day_type) {
        this.timetable_day_type = timetable_day_type;
    }

    public int getTimetable_layout_type() {
        return timetable_layout_type;
    }

    public void setTimetable_layout_type(int timetable_layout_type) {
        this.timetable_layout_type = timetable_layout_type;
    }

    public String getUploaded_at() {
        return uploaded_at;
    }

    public void setUploaded_at(String uploaded_at) {
        this.uploaded_at = uploaded_at;
    }

    public String getTimetable_version() {
        return timetable_version;
    }

    public void setTimetable_version(String timetable_version) {
        this.timetable_version = timetable_version;
    }

    public String getTimetable_updated() {
        return timetable_updated;
    }

    public void setTimetable_updated(String timetable_updated) {
        this.timetable_updated = timetable_updated;
    }

}
