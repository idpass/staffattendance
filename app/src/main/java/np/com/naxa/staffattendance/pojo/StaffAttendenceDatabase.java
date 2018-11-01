package np.com.naxa.staffattendance.pojo;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Staff.class}, version = 2, exportSchema = false)
public abstract class StaffAttendenceDatabase extends RoomDatabase {

    private static StaffAttendenceDatabase INSTANCE;

    public abstract StaffDao staffDao();

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
