package np.com.naxa.staffattendance.attendence;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "attendance")
public class Attendance {

    private int id;

    private String staffIds;

    @PrimaryKey
    @NonNull
    private String attendanceDate;

    private String teamId;

    private String submittedBy;

    private String createdAt;

    private String syncStatus;

    private String updatedAt;

    public Attendance(@NonNull int id, String staffIds, String attendanceDate, String teamId, String submittedBy, String createdAt, String syncStatus, String updatedAt) {
        this.id = id;
        this.staffIds = staffIds;
        this.attendanceDate = attendanceDate;
        this.teamId = teamId;
        this.submittedBy = submittedBy;
        this.createdAt = createdAt;
        this.syncStatus = syncStatus;
        this.updatedAt = updatedAt;
    }

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public String getStaffIds() {
        return staffIds;
    }

    public void setStaffIds(String staffIds) {
        this.staffIds = staffIds;
    }

    public String getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(String attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
