package Room_Planner;

public class ColumnData {
    private String planClient;
    private String planSite;
    private String planRoom;
    private String planDate;
    private String planColumn;

    public ColumnData()
    {
    }

    public ColumnData(String client, String site, String room, String date)
    {
        planClient = client;
        planSite = site;
        planRoom = room;
        planDate = date;
        //planColumn = column;
    }

    public String getPlanClient() {
        return planClient;
    }

    public void setPlanClient(String planClient) {
        this.planClient = planClient;
    }

    public String getPlanSite() {
        return planSite;
    }

    public void setPlanSite(String planSite) {
        this.planSite = planSite;
    }

    public String getPlanRoom() {
        return planRoom;
    }

    public void setPlanRoom(String planRoom) {
        this.planRoom = planRoom;
    }

    public String getPlanDate() {
        return planDate;
    }

    public void setPlanDate(String planDate) {
        this.planDate = planDate;
    }

    public String getPlanColumn() {
        return planColumn;
    }

    public void setPlanColumn(String planColumn) {
        this.planColumn = planColumn;
    }

}
