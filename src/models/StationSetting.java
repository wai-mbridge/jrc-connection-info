package models;

public class StationSetting {
    private int ID;
    private String station_name;
    private int connection_grace_time;
    private int connection_extract_count;

    public StationSetting() {
        super();
    }

    public StationSetting(String station_name, int connection_grace_time, int connection_extract_count) {
        super();
        this.station_name = station_name;
        this.connection_grace_time = connection_grace_time;
        this.connection_extract_count = connection_extract_count;
    }

    public int getID() {
        return ID;
    }

    public void setID(int iD) {
        ID = iD;
    }

    public String getStation_name() {
        return station_name;
    }

    public void setStation_name(String station_name) {
        this.station_name = station_name;
    }

    public int getConnection_grace_time() {
        return connection_grace_time;
    }

    public void setConnection_grace_time(int connection_grace_time) {
        this.connection_grace_time = connection_grace_time;
    }

    public int getConnection_extract_count() {
        return connection_extract_count;
    }

    public void setConnection_extract_count(int connection_extract_count) {
        this.connection_extract_count = connection_extract_count;
    }

}
