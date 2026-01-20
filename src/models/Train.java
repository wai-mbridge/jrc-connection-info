package models;

import java.util.List;

public class Train {

    private int id;
    private int route_section_id;
    private String train_number;
    private int bound_type;
    private int day_type;
    private int train_type;
    private String first_station;
    private String last_station;
    private String supplement;

    private RouteSection route_section;
    private List<TrainStation> train_stations;

    public Train() {
        super();
    }

    public Train(int id, int route_section_id, String train_number, int bound_type, int day_type, int train_type,
            String first_station, String last_station, String supplement, RouteSection route_section,
            List<TrainStation> train_stations) {
        super();
        this.id = id;
        this.route_section_id = route_section_id;
        this.train_number = train_number;
        this.bound_type = bound_type;
        this.day_type = day_type;
        this.train_type = train_type;
        this.first_station = first_station;
        this.last_station = last_station;
        this.supplement = supplement;
        this.route_section = route_section;
        this.train_stations = train_stations;
    }

    public RouteSection getRoute_section() {
        return route_section;
    }

    public void setRoute_section(RouteSection route_section) {
        this.route_section = route_section;
    }

    public List<TrainStation> getTrain_stations() {
        return train_stations;
    }

    public void setTrain_stations(List<TrainStation> train_stations) {
        this.train_stations = train_stations;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRoute_section_id() {
        return route_section_id;
    }

    public void setRoute_section_id(int route_section_id) {
        this.route_section_id = route_section_id;
    }

    public String getTrain_number() {
        return train_number;
    }

    public void setTrain_number(String train_number) {
        this.train_number = train_number;
    }

    public int getBound_type() {
        return bound_type;
    }

    public void setBound_type(int bound_type) {
        this.bound_type = bound_type;
    }

    public int getDay_type() {
        return day_type;
    }

    public void setDay_type(int day_type) {
        this.day_type = day_type;
    }

    public int getTrain_type() {
        return train_type;
    }

    public void setTrain_type(int train_type) {
        this.train_type = train_type;
    }

    public String getFirst_station() {
        return first_station;
    }

    public void setFirst_station(String first_station) {
        this.first_station = first_station;
    }

    public String getLast_station() {
        return last_station;
    }

    public void setLast_station(String last_station) {
        this.last_station = last_station;
    }

    public String getSupplement() {
        return supplement;
    }

    public void setSupplement(String supplement) {
        this.supplement = supplement;
    }

}
