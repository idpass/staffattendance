package np.com.naxa.staffattendance.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import np.com.naxa.staffattendance.SharedPreferenceUtils;
import np.com.naxa.staffattendance.application.StaffAttendance;

public class TeamDao {

    private final String TABLE_NAME = DatabaseHelper.TABLE_STAFF;

    public Cursor getCursor(boolean distinct, String selection, String[] selectionArgs) {
        SQLiteDatabase db = DatabaseHelper.getDatabaseHelper().getWritableDatabase();
        return db.query(true, TABLE_NAME, null, selection, selectionArgs, null, null, null, null);
    }


    public Cursor getCursor(String selection, String[] selectionArgs) {
        return getCursor(false, selection, selectionArgs);
    }

    public String getTeamNameById(String id) {
        String teamName = "";
        Cursor cursor = null;
        cursor = DatabaseHelper.getDatabaseHelper().getWritableDatabase()
                .query(true, TABLE_NAME, null, null, null, null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            teamName = DatabaseHelper.getStringFromCursor(cursor, DatabaseHelper.KEY_STAFF_TEAM_NAME);
        }
        return teamName;
    }

    public String getOneTeamIdForDemo() {

        return SharedPreferenceUtils.getFromPrefs(StaffAttendance.getStaffAttendance().getApplicationContext(), "TEAM_ID", "");

        //        SQLiteDatabase db = DatabaseHelper.getDatabaseHelper().getWritableDatabase();
//        Cursor cursor = null;
//        String teamId = "";
//
//        try {
//            cursor = db.query(true, TABLE_NAME, null, null, null, null, null, null, null);
//            cursor.moveToFirst();
//            teamId = DatabaseHelper.getStringFromCursor(cursor, DatabaseHelper.KEY_STAFF_TEAM_ID);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//
//            if (cursor != null) {
//                cursor.close();
//                db.close();
//            }
//
//            if (db != null) {
//                db.close();
//            }
//        }
//
//        return teamId;

    }
}



