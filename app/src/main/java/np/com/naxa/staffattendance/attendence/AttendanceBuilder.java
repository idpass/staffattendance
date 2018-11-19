package np.com.naxa.staffattendance.attendence;

public class AttendanceBuilder {
    private int id;
    private String staffIds;
    private String attendanceDate;
    private String teamId;
    private String submittedBy;
    private String createdAt;
    private String syncStatus;
    private String updatedAt;

    public AttendanceBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public AttendanceBuilder setStaffIds(String staffIds) {
        this.staffIds = staffIds;
        return this;
    }

    public AttendanceBuilder setAttendanceDate(String attendanceDate) {
        this.attendanceDate = attendanceDate;
        return this;
    }

    public AttendanceBuilder setTeamId(String teamId) {
        this.teamId = teamId;
        return this;
    }

    public AttendanceBuilder setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
        return this;
    }

    public AttendanceBuilder setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public AttendanceBuilder setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
        return this;
    }

    public AttendanceBuilder setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Attendance createAttendance() {
        return new Attendance(id, staffIds, attendanceDate, teamId, submittedBy, createdAt, syncStatus, updatedAt);
    }
}