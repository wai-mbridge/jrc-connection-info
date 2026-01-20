package models;

public class TrainStation {
    private int id;
    private int train_id;
    private int station_position;
    private String station_name;
    private String arrival_time;
    private String departure_time;
    private String platform;
    private int stop_type;

    private Train train;

    public TrainStation() {
        super();
    }

    public TrainStation(int id, int train_id, int station_position, String station_name, String arrival_time,
            String departure_time, String platform, int stop_type, Train train) {
        super();
        this.id = id;
        this.train_id = train_id;
        this.station_position = station_position;
        this.station_name = station_name;
        this.arrival_time = arrival_time;
        this.departure_time = departure_time;
        this.platform = platform;
        this.stop_type = stop_type;
        this.train = train;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTrain_id() {
        return train_id;
    }

    public void setTrain_id(int train_id) {
        this.train_id = train_id;
    }

    public int getStation_position() {
        return station_position;
    }

    public void setStation_position(int station_position) {
        this.station_position = station_position;
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

    public int getStop_type() {
        return stop_type;
    }

    public void setStop_type(int stop_type) {
        this.stop_type = stop_type;
    }

}
