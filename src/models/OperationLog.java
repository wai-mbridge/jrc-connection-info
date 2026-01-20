package models;

public class OperationLog {

    private int id;
    private int operation_type;
    private String operated_at;

    public OperationLog() {
        super();
    }

    public OperationLog(int id, int operation_type, String operated_at) {
        super();
        this.id = id;
        this.operation_type = operation_type;
        this.operated_at = operated_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOperation_type() {
        return operation_type;
    }

    public void setOperation_type(int operation_type) {
        this.operation_type = operation_type;
    }

    public String getOperated_at() {
        return operated_at;
    }

    public void setOperated_at(String operated_at) {
        this.operated_at = operated_at;
    }

}
