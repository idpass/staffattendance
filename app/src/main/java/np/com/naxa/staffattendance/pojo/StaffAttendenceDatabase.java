package np.com.naxa.staffattendance.pojo;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import np.com.naxa.staffattendance.attendence.Attendance;
import np.com.naxa.staffattendance.attendence.AttendanceDao;

@Database(entities = {Staff.class, Attendance.class}, version = 3, exportSchema = false)
public abstract class StaffAttendenceDatabase extends RoomDatabase {

    private static StaffAttendenceDatabase INSTANCE;

    public abstract StaffDao staffDao();
    public abstract AttendanceDao attendenceDao();

    public static StaffAttendenceDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (StaffAttendenceDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            StaffAttendenceDatabase.class, "staff_attendence")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
