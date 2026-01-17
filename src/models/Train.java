package models;

import java.util.List;

public class Train {
    private int ID;
    private int route_section_id;
    private String train_number;
    private String weekday_holiday_type;
    private String up_down_type;
    private String train_type;
    private String start_station;
    private String end_station;
    private String remark;

    private RouteSection route_section;
    private List<TrainStation> train_stations;

    public Train() {
        super();
    }

    public Train(int iD, int route_section_id, String train_number, String weekday_holiday_type, String up_down_type,
            String train_type, String start_station, String end_station, String remark) {
        super();
        ID = iD;
        this.route_section_id = route_section_id;
        this.train_number = train_number;
        this.weekday_holiday_type = weekday_holiday_type;
        this.up_down_type = up_down_type;
        this.train_type = train_type;
        this.start_station = start_station;
        this.end_station = end_station;
        this.remark = remark;
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

    public int getID() {
        return ID;
    }

    public void setID(int iD) {
        ID = iD;
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

    public String getWeekday_holiday_type() {
        return weekday_holiday_type;
    }

    public void setWeekday_holiday_type(String weekday_holiday_type) {
        this.weekday_holiday_type = weekday_holiday_type;
    }

    public String getUp_down_type() {
        return up_down_type;
    }

    public void setUp_down_type(String up_down_type) {
        this.up_down_type = up_down_type;
    }

    public String getTrain_type() {
        return train_type;
    }

    public void setTrain_type(String train_type) {
        this.train_type = train_type;
    }

    public String getStart_station() {
        return start_station;
    }

    public void setStart_station(String start_station) {
        this.start_station = start_station;
    }

    public String getEnd_station() {
        return end_station;
    }

    public void setEnd_station(String end_station) {
        this.end_station = end_station;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
