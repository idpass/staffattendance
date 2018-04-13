package np.com.naxa.staffattendance.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TeamDao {

    private final String TABLE_NAME = DatabaseHelper.TABLE_STAFF;

    public Cursor getCursor(boolean distinct, String selection, String[] selectionArgs) {
        SQLiteDatabase db = DatabaseHelper.getDatabaseHelper().getWritableDatabase();
        return db.query(true, TABLE_NAME, null, selection, selectionArgs, null, null, null, null);
    }

    public String getOneTeamIdForDemo() {
        SQLiteDatabase db = DatabaseHelper.getDatabaseHelper().getWritableDatabase();
        Cursor cursor = null;
        String teamId = "";

        try {
            cursor = db.query(true, TABLE_NAME, null, null, null, null, null, null, null);
            cursor.moveToFirst();
            teamId = DatabaseHelper.getStringFromCursor(cursor, DatabaseHelper.KEY_STAFF_TEAM_ID);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
                db.close();
            }

            if (db != null) {
                db.close();
            }
        }

        return teamId;

    }

}
