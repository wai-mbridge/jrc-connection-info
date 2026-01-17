package models;

public class TrainStation {
    private int ID;
    private int train_id;
    private String station_number;
    private String station_name;
    private String arrival_time;
    private String departure_time;
    private String platform;
    private String stop_type;

    private Train train;

    public TrainStation() {
        super();
    }

    public TrainStation(int iD, int train_id, String station_number, String station_name, String arrival_time,
            String departure_time, String platform, String stop_type) {
        super();
        ID = iD;
        this.train_id = train_id;
        this.station_number = station_number;
        this.station_name = station_name;
        this.arrival_time = arrival_time;
        this.departure_time = departure_time;
        this.platform = platform;
        this.stop_type = stop_type;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    public int getID() {
        return ID;
    }

    public void setID(int iD) {
        ID = iD;
    }

    public int getTrain_id() {
        return train_id;
    }

    public void setTrain_id(int train_id) {
        this.train_id = train_id;
    }

    public String getStation_number() {
        return station_number;
    }

    public void setStation_number(String station_number) {
        this.station_number = station_number;
    }

    public String getStation_name() {
        return station_name;
    }

    public void setStation_name(String station_name) {
        this.station_name = station_name;
    }

    public String getArrival_time() {
        return arrival_time;
    }

    public void setArrival_time(String arrival_time) {
        this.arrival_time = arrival_time;
    }

    public String getDeparture_time() {
        return departure_time;
    }

    public void setDeparture_time(String departure_time) {
        this.departure_time = departure_time;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getStop_type() {
        return stop_type;
    }

    public void setStop_type(String stop_type) {
        this.stop_type = stop_type;
    }

}
