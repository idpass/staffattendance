package np.com.naxa.staffattendance.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import np.com.naxa.staffattendance.attendence.AttedanceResponse;
import np.com.naxa.staffattendance.attendence.TeamMemberResposne;
import np.com.naxa.staffattendance.utlils.DateConvertor;

public class AttendanceDao {

    public final static class SyncStatus {
        public static String FINALIZED = "finalized";
        public static String UPLOADED = "uploaded";
    }


    private final String TABLE_NAME = DatabaseHelper.TABLE_ATTENDANCE;

    public ContentValues getContentValuesForAttedance(AttedanceResponse attedance) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.KEY_ID, attedance.getId());

        String date;
        if (attedance.getAttendanceDate().equalsIgnoreCase("Today")) {
            date = DateConvertor.getCurrentDate();
        } else {
            date = attedance.getAttendanceDate();
        }

        contentValues.put(DatabaseHelper.KEY_ATTENDACE_DATE, date);
        contentValues.put(DatabaseHelper.KEY_SYNC_STATUS, attedance.getDataSyncStatus());
        contentValues.put(DatabaseHelper.KEY_STAFFS_IDS, attedance.getStaffs().toString());
        return contentValues;
    }

    public void saveAttendance(List<AttedanceResponse> attedanceResponses) {
        SQLiteDatabase db = DatabaseHelper.getDatabaseHelper().getWritableDatabase();
        try {
            db.beginTransaction();
            for (AttedanceResponse staff : attedanceResponses) {

                ContentValues values = getContentValuesForAttedance(staff);
                saveAttedance(db, values);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    private long saveAttedance(SQLiteDatabase database, ContentValues contentValues) {
        return database.replace(TABLE_NAME, null, contentValues);
    }

    public long saveAttedance(ContentValues contentValues) {
        return DatabaseHelper.getDatabaseHelper().getWritableDatabase().replace(TABLE_NAME, null, contentValues);
    }

    public ArrayList<AttedanceResponse> getAttendanceFromCursor(Cursor cursor) {
        ArrayList<AttedanceResponse> attedanceResponses = new ArrayList<>();

        if (cursor == null || cursor.getCount() <= 0) {
            return attedanceResponses;
        }

        while (cursor.moveToNext()) {
            AttedanceResponse attedanceResponse = new AttedanceResponse();
            String attendanceDate = DatabaseHelper.getStringFromCursor(cursor, DatabaseHelper.KEY_ATTENDACE_DATE);
            attedanceResponse.setAttendanceDate(attendanceDate);

            String staffIDs = DatabaseHelper.getStringFromCursor(cursor, DatabaseHelper.KEY_STAFFS_IDS);
            String[] staffIDlist = staffIDs.replace("[", "").replace("]", "").split(",");
            attedanceResponse.setStaffs(Arrays.asList(staffIDlist));


            attedanceResponses.add(attedanceResponse);

        }

        return attedanceResponses;
    }


    public Cursor getCursor(String selection, String[] selectionArgs) {
        SQLiteDatabase db = DatabaseHelper.getDatabaseHelper().getWritableDatabase();
        return db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null, null);
    }

    public List<AttedanceResponse> getTodaysAddedance(String teamId) {
        return getAttedanceByDate(teamId, DateConvertor.getCurrentDate());
    }

    public List<AttedanceResponse> getAttedanceByDate(String teamId, String date) {
        Cursor cursor = getCursor(DatabaseHelper.KEY_ATTENDACE_DATE + "=?", new String[]{date});
        return getAttendanceFromCursor(cursor);
    }


    public ArrayList<AttedanceResponse> getAttendanceSheetForTeam(String teamId) {
        Cursor cursor = getCursor(null, null);
        ArrayList<AttedanceResponse> list = getAttendanceFromCursor(cursor);
        closeCursor(cursor);
        return list;
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
