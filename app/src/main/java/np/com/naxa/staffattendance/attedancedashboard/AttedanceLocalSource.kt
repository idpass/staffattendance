package np.com.naxa.staffattendance.attedancedashboard

import np.com.naxa.staffattendance.attendence.AttendanceResponse
import np.com.naxa.staffattendance.database.AttendanceDao

class AttedanceLocalSource {

    val attedanceDao = AttendanceDao()

    companion object {
        val instance = AttedanceLocalSource()
    }


    fun getAttendanceForDate(date: String?, new_attendance: AttendanceResponse, team_id: String?): AttendanceResponse? {
        val oldAttendance = attedanceDao.getAttedanceByDate(team_id, date)
        if (oldAttendance.presentStaffIds != null && oldAttendance.presentStaffIds.size > 0) {
            oldAttendance.setStaffs(oldAttendance.presentStaffIds + new_attendance.presentStaffIds)
            return oldAttendance
        }

        return new_attendance
    }

    fun updateAttendance(date: String?, new_attendance: AttendanceResponse, team_id: String?) {
        val attendanceResponse = getAttendanceForDate(date, new_attendance, team_id)
        val contentValues = attedanceDao.getContentValuesForAttedance(attendanceResponse)
        attedanceDao.saveAttedance(contentValues)
    }

}