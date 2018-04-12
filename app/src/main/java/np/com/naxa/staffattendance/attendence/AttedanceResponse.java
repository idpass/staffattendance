package np.com.naxa.staffattendance.attendence;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AttedanceResponse {

    private List<TeamMemberResposne> teamMemberResposnes = null;

    @SerializedName("id")
    @Expose
    private Integer id;


    @SerializedName("attendance_date")
    @Expose
    private String attendanceDate;
    @SerializedName("is_deleted")
    @Expose
    private Boolean isDeleted;
    @SerializedName("staffs")
    @Expose
    private List<Integer> staffs = null;

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

    public List<Integer> getStaffs() {
        return staffs;
    }

    public void setStaffs(List<Integer> staffs) {
        this.staffs = staffs;
    }

    public List<TeamMemberResposne> getTeamMemberResposnes() {
        return teamMemberResposnes;
    }

    public void setTeamMemberResposnes(List<TeamMemberResposne> teamMemberResposnes) {
        this.teamMemberResposnes = teamMemberResposnes;
    }
}
