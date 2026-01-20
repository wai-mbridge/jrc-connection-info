package models;

public class StationSetting {
    private int id;
    private String station_name;
    private int duration;
    private int extraction;

    public StationSetting() {
        super();
    }

    public StationSetting(String station_name, int duration, int extraction) {
        super();
        this.station_name = station_name;
        this.duration = duration;
        this.extraction = extraction;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStation_name() {
        return station_name;
    }

    public void setStation_name(String station_name) {
        this.station_name = station_name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getExtraction() {
        return extraction;
    }

    public void setExtraction(int extraction) {
        this.extraction = extraction;
    }

}
