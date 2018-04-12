package np.com.naxa.staffattendance.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import np.com.naxa.staffattendance.R;
import np.com.naxa.staffattendance.application.StaffAttendance;
import np.com.naxa.staffattendance.attendence.AttedanceResponse;
import np.com.naxa.staffattendance.attendence.TeamMemberResposne;
import np.com.naxa.staffattendance.attendence.TeamMemberResposneBuilder;

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
            db.close();
        }
    }

    private long saveStaff(SQLiteDatabase database, ContentValues contentValues) {
        return database.replace(TABLE_NAME, null, contentValues);
    }

    public ArrayList<AttedanceResponse> getAttendanceFromCursor(Cursor cursor) {
        ArrayList<AttedanceResponse> attedanceResponses = new ArrayList<>();

        if (cursor == null || cursor.getCount() <= 0) {
            return attedanceResponses;
        }

        while (cursor.moveToNext()) {

            String attendanceDate = DatabaseHelper.getStringFromCursor(cursor, DatabaseHelper.KEY_ATTENDACE_DATE);

            AttedanceResponse attedanceResponse = new AttedanceResponse();
            attedanceResponse.setAttendanceDate(attendanceDate);

            attedanceResponses.add(attedanceResponse);
        }

        return attedanceResponses;
    }



    public Cursor getCursor(String selection, String[] selectionArgs) {
        SQLiteDatabase db = DatabaseHelper.getDatabaseHelper().getWritableDatabase();
        return db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null, null);
    }

    private void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }

    private void closeDB(SQLiteDatabase db) {
        if (db != null) {
            db.close();
        }
    }
}
