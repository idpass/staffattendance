package np.com.naxa.staffattendance.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;


import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import np.com.naxa.staffattendance.R;
import np.com.naxa.staffattendance.application.StaffAttendance;
import np.com.naxa.staffattendance.attendence.TeamMemberResposne;
import np.com.naxa.staffattendance.staff.Staff;


/**
 * Created by samir on 4/1/2018.
 */

public class StaffDao {


    private static StaffDao staffDao;

    private final String TABLE_NAME = DatabaseHelper.TABLE_STAFF;


    public static StaffDao getInstance() {
        if (staffDao == null) {
            return new StaffDao();
        }

        return staffDao;
    }

    public long saveStaff(TeamMemberResposne staff) {
        return saveStaff(getContentValuesFronSaff(staff));
    }

    public void removeAllStaffList() {
        SQLiteDatabase db = DatabaseHelper.getDatabaseHelper().getWritableDatabase();
        db.execSQL("DELETE from " + TABLE_NAME);
        db.close();
    }

    public void saveStafflist(final List<TeamMemberResposne> staffs) {

        SQLiteDatabase db = DatabaseHelper.getDatabaseHelper().getWritableDatabase();
        try {
            db.beginTransaction();
            for (TeamMemberResposne staff : staffs) {

                ContentValues values = getContentValuesFronSaff(staff);
                saveStaff(db, values);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }


    private ContentValues getContentValuesFronSaff(TeamMemberResposne staff) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.KEY_STAFF_FULL_NAME, staff.getFirstName().concat(" ").concat(staff.getLastName()));
        contentValues.put(DatabaseHelper.KEY_STAFF_TYPE, StaffAttendance.getStaffAttendance().getString(R.string.not_avaliable, "Staff type"));
        contentValues.put(DatabaseHelper.KEY_ID, staff.getId());
        contentValues.put(DatabaseHelper.KEY_STAFF_TEAM_ID, staff.getTeamID());
        contentValues.put(DatabaseHelper.KEY_STAFF_TEAM_NAME, staff.getTeamName());
        return contentValues;
    }


    public List<Staff> getStaffByTeamId(String staffID) {
        Cursor cursor = getCursor(DatabaseHelper.KEY_STAFF_TEAM_ID + "=?", new String[]{staffID});
        return getStaffFromCursor(cursor);
    }

    public List<Staff> getStaffByTeamAndStaffId(String teamId, String staffID) {
        Cursor cursor = getCursor(DatabaseHelper.KEY_ID + "=?", new String[]{staffID});
        return getStaffFromCursor(cursor);
    }

    public Cursor getCursor(String selection, String[] selectionArgs) {
        SQLiteDatabase db = DatabaseHelper.getDatabaseHelper().getWritableDatabase();
        return db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null, null);
    }

    public ArrayList<Staff> getStaffFromCursor(Cursor cursor) {
        ArrayList<Staff> staffs = new ArrayList<>();

        if (cursor == null || cursor.getCount() <= 0) {
            return staffs;
        }

        while (cursor.moveToNext()) {

            String teamID = DatabaseHelper.getStringFromCursor(cursor, DatabaseHelper.KEY_STAFF_TEAM_ID);
            String teamName = DatabaseHelper.getStringFromCursor(cursor, DatabaseHelper.KEY_STAFF_TEAM_NAME);
            String staffId = DatabaseHelper.getStringFromCursor(cursor, DatabaseHelper.KEY_ID);
            String staffName = DatabaseHelper.getStringFromCursor(cursor, DatabaseHelper.KEY_STAFF_FULL_NAME);

            Staff staff = new Staff();
            staff.setTeamID(teamID);
            staff.setTeamName(teamName);
            staff.setFirstName(staffName);

            //                    .setTeamID(teamID)
//                    .setTeamName(teamName)
//                    .setId(staffId)
//                    .setFirstName(staffName)
//                    .createTeamMemberResposne();

            staffs.add(staff);
        }

        return staffs;
    }

    private long saveStaff(ContentValues contentValues) {
        return DatabaseHelper.getDatabaseHelper().getWritableDatabase().insert(DatabaseHelper.TABLE_STAFF, null, contentValues);
    }

    private long saveStaff(SQLiteDatabase database, ContentValues contentValues) {
        return database.replace(DatabaseHelper.TABLE_STAFF, null, contentValues);
    }


    public Observable<List<String>> getStaffIdFromObject(ArrayList<TeamMemberResposne> staffs) {
        return Observable.just(staffs)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapIterable(new Function<ArrayList<TeamMemberResposne>, Iterable<TeamMemberResposne>>() {
                    @Override
                    public Iterable<TeamMemberResposne> apply(ArrayList<TeamMemberResposne> teamMemberResposnes) {
                        return teamMemberResposnes;
                    }
                }).flatMap(new Function<TeamMemberResposne, Observable<String>>() {
                    @Override
                    public Observable<String> apply(TeamMemberResposne teamMemberResposne) {
                        return Observable.just(teamMemberResposne.getId());
                    }
                }).toList()
                .toObservable();
    }


}
