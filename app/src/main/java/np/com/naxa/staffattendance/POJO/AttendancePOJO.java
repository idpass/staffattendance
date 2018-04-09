package np.com.naxa.staffattendance.POJO;

/**
 * Created by samir on 4/1/2018.
 */

public class AttendancePOJO {

    String date;
    String staffID;
    String staffName;
    String submitted_by;

    public AttendancePOJO(String date, String staffID, String staffName, String submitted_by) {
        this.date = date;
        this.staffID = staffID;
        this.staffName = staffName;
        this.submitted_by = submitted_by;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStaffID() {
        return staffID;
    }

    public void setStaffID(String staffID) {
        this.staffID = staffID;
    }

    public String getSubmitted_by() {
        return submitted_by;
    }

    public void setSubmitted_by(String submitted_by) {
        this.submitted_by = submitted_by;
    }


}
