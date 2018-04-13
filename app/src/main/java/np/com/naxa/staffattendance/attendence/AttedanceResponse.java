package np.com.naxa.staffattendance.attendence;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AttedanceResponse {

    private List<TeamMemberResposne> teamMemberResposnes = null;

    @SerializedName("id")
    @Expose
    private Integer id;

    public AttedanceResponse(String attendanceDate, List<String> staffs) {
        this.attendanceDate = attendanceDate;
        this.staffs = staffs;
    }

    public AttedanceResponse() {

    }

    @SerializedName("attendance_date")
    @Expose
    private String attendanceDate;
    @SerializedName("is_deleted")
    @Expose
    private Boolean isDeleted;
    @SerializedName("staffs")
    @Expose
    private List<String> staffs = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(String attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public List<String> getStaffs() {
        return staffs;
    }

    public void setStaffs(List<String> staffs) {
        this.staffs = staffs;
    }

    public List<TeamMemberResposne> getTeamMemberResposnes() {
        return teamMemberResposnes;
    }

    public void setTeamMemberResposnes(List<TeamMemberResposne> teamMemberResposnes) {
        this.teamMemberResposnes = teamMemberResposnes;
    }
}
