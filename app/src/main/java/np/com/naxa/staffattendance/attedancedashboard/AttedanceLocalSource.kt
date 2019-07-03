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
//        val hasAttedanceStarted = oldAttendance.presentStaffIds != null && oldAttendance.presentStaffIds.size > 0
        val hasAttedanceStarted = oldAttendance.idPassProofs != null && oldAttendance.idPassProofs.size > 0
//        val attendanceNotTaken = !oldAttendance.presentStaffIds.contains(new_attendance.presentStaffIds[0].toString())//we always add one staff at a time so [0] should be safe
        val attendanceNotTaken = oldAttendance.idPassProofs != null && !oldAttendance.idPassProofs.containsKey(new_attendance.presentStaffIds[0]);

        if (hasAttedanceStarted && attendanceNotTaken) {
            oldAttendance.idPassProofs.putAll(new_attendance.idPassProofs);
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