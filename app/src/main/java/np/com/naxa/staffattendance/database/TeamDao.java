package np.com.naxa.staffattendance.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import np.com.naxa.staffattendance.SharedPreferenceUtils;
import np.com.naxa.staffattendance.application.StaffAttendance;

public class TeamDao {

    private final String TABLE_NAME = DatabaseHelper.TABLE_STAFF;


    private static TeamDao teamDao;

    public static TeamDao getInstance() {
        if (teamDao == null) {
            return new TeamDao();
        }

        return teamDao;
    }

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

        return SharedPreferenceUtils.getFromPrefs(StaffAttendance.getStaffAttendance().getApplicationContext(), SharedPreferenceUtils.KEY.TeamID, "");

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

//        return teamId;

    }

    public String getTeamMembers(List<String> membersIds) {
        StringBuilder builder = new StringBuilder();
        for (String id : membersIds) {
            Cursor cursor = getCursor(DatabaseHelper.KEY_ID + "=?", new String[]{id});

            if (cursor.getCount() != 1) break;
            cursor.moveToFirst();
            builder.append("\n");
            builder.append("+ ");
            builder.append(DatabaseHelper.getStringFromCursor(cursor, DatabaseHelper.KEY_STAFF_FULL_NAME));
        }

        return builder.toString();

    }
}



