package models;

public class Logs {

    private int ID;
    private String action_type;
    private String executed_at;

    public Logs() {
        super();
    }

    public Logs(int iD, String action_type, String executed_at) {
        super();
        ID = iD;
        this.action_type = action_type;
        this.executed_at = executed_at;
    }

    public int getID() {
        return ID;
    }

    public void setID(int iD) {
        ID = iD;
    }

    public String getAction_type() {
        return action_type;
    }

    public void setAction_type(String action_type) {
        this.action_type = action_type;
    }

    public String getExecuted_at() {
        return executed_at;
    }

    public void setExecuted_at(String executed_at) {
        this.executed_at = executed_at;
    }

}
