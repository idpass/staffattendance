package np.com.naxa.staffattendance.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import np.com.naxa.staffattendance.attendence.AttedanceResponse;
import np.com.naxa.staffattendance.utlils.DateConvertor;
import rx.Observable;

public class AttendanceDao {

    public void updateAttendance(String date, String teamId) {
        String selection = DatabaseHelper.KEY_ATTENDACE_DATE + "=? AND " + DatabaseHelper.KEY_STAFF_TEAM_ID + "=?";
        String[] selectionArgs = new String[]{date, teamId};
        updateAttendance(getContentValuesForStatusUpdate(), selection, selectionArgs);
    }


    public ContentValues getContentValuesForStatusUpdate() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.KEY_SYNC_STATUS, SyncStatus.UPLOADED);
        return contentValues;
    }

    private void updateAttendance(ContentValues contentValues, String selection, String[] selectionArgs) {
        DatabaseHelper.getDatabaseHelper().getWritableDatabase().update(TABLE_NAME, contentValues, selection, selectionArgs);
    }

    public void removeAllAttedance() {
        SQLiteDatabase db = DatabaseHelper.getDatabaseHelper().getWritableDatabase();
        db.execSQL("DELETE from " + TABLE_NAME + " WHERE " + DatabaseHelper.KEY_SYNC_STATUS + " != '" + SyncStatus.FINALIZED + "'");
        db.close();
    }

    public final static class SyncStatus {
        public static String FINALIZED = "finalized";
        public static String UPLOADED = "uploaded";
    }


    private final String TABLE_NAME = DatabaseHelper.TABLE_ATTENDANCE;

    public ContentValues getContentValuesForAttedance(AttedanceResponse attedance) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.KEY_ID, attedance.getId());

        String date;
        if (attedance.getAttendanceDate(false).equalsIgnoreCase("Today")) {
            date = DateConvertor.getCurrentDate();
        } else {
            date = attedance.getAttendanceDate(false);
        }

        contentValues.put(DatabaseHelper.KEY_ATTENDACE_DATE, date);
        contentValues.put(DatabaseHelper.KEY_SYNC_STATUS, attedance.getDataSyncStatus());
        contentValues.put(DatabaseHelper.KEY_STAFFS_IDS, attedance.getPresentStaffIds().toString());
        return contentValues;
    }

    public Observable<?> saveAttendance(List<AttedanceResponse> attedanceResponses) {
        SQLiteDatabase db = DatabaseHelper.getDatabaseHelper().getWritableDatabase();
        try {
            db.beginTransaction();
            for (AttedanceResponse staff : attedanceResponses) {

                ContentValues values = getContentValuesForAttedance(staff);
                long i = saveAttedance(db, values);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
        return null;
    }

    private long saveAttedance(SQLiteDatabase database, ContentValues contentValues) {
        return database.replace(TABLE_NAME, null, contentValues);
    }

    public long saveAttedance(ContentValues contentValues) {
        return DatabaseHelper.getDatabaseHelper().getWritableDatabase().replace(TABLE_NAME, null, contentValues);
    }


    public AttedanceResponse getSingleAttedanceFromCusor(Cursor cursor) {
        ArrayList<AttedanceResponse> list = getAttendanceFromCursor(cursor);
        if (list != null && list.size() >= 1) {
            return list.get(0);
        }

        return new AttedanceResponse();
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

    public AttedanceResponse getTodaysAddedance(String teamId) {
        return getAttedanceByDate(teamId, DateConvertor.getCurrentDate());
    }

    public AttedanceResponse getAttedanceByDate(String teamId, String date) {
        Cursor cursor = getCursor(DatabaseHelper.KEY_ATTENDACE_DATE + "=?", new String[]{date});
        return getSingleAttedanceFromCusor(cursor);
    }


    public ArrayList<AttedanceResponse> getAttendanceSheetForTeam(String teamId) {
        Cursor cursor = getCursor(null, null);
        ArrayList<AttedanceResponse> list = getAttendanceFromCursor(cursor);
        closeCursor(cursor);
        return list;
    }

    public ArrayList<AttedanceResponse> getFinalizedAttendanceSheet() {
        Cursor cursor = getCursor(DatabaseHelper.KEY_SYNC_STATUS + "=?", new String[]{SyncStatus.FINALIZED});
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

    public List<Pair<Integer, String>> getAllUnfinilizedAttendanceListInPair() {

        List<Pair<Integer, String>> pairList = new ArrayList<>();
        //todo need to add where to get unfililized from
        Cursor cursor = DatabaseHelper.getDatabaseHelper().getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_NAME, null);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            Pair<Integer, String> pair = new Pair<>(cursor.getInt(0), cursor.getString(1));
            pairList.add(pair);
        }
        closeCursor(cursor);
        return pairList;
    }

    public void updateTableAfterFinilizingForm(List<Pair<Integer, String>> pairList) {
        SQLiteDatabase writer = DatabaseHelper.getDatabaseHelper().getWritableDatabase();
        for (Pair<Integer, String> pair : pairList) {
            writer.rawQuery("UPDATE "
                    + TABLE_NAME
                    + " SET "
                    + DatabaseHelper.KEY_STAFFS_IDS
                    + " = "
                    + pair.second
                    + " WHERE "
                    + DatabaseHelper.KEY_ID
                    + " = "
                    + pair.first, null);
        }
        closeDB(writer);
    }

}
