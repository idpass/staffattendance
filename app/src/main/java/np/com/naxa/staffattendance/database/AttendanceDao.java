package np.com.naxa.staffattendance.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import np.com.naxa.staffattendance.R;
import np.com.naxa.staffattendance.application.StaffAttendance;
import np.com.naxa.staffattendance.attendence.AttedanceResponse;
import np.com.naxa.staffattendance.attendence.TeamMemberResposne;

public class AttendanceDao {
    private final String TABLE_NAME = DatabaseHelper.TABLE_ATTENDANCE;

    private ContentValues getContentValuesForAttedance(AttedanceResponse attedance) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.KEY_ID, attedance.getId());
        contentValues.put(DatabaseHelper.KEY_ATTENDACE_DATE, attedance.getAttendanceDate());
        contentValues.put(DatabaseHelper.KEY_STAFFS_IDS, attedance.getStaffs().toString());
        return contentValues;
    }

    public void saveAttendance(List<AttedanceResponse> attedanceResponses) {
        SQLiteDatabase db = DatabaseHelper.getDatabaseHelper().getWritableDatabase();
        try {
            db.beginTransaction();
            for (AttedanceResponse staff : attedanceResponses) {

                ContentValues values = getContentValuesForAttedance(staff);
                saveStaff(db, values);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    private long saveStaff(SQLiteDatabase database, ContentValues contentValues) {
        return database.replace(TABLE_NAME, null, contentValues);
    }
}
