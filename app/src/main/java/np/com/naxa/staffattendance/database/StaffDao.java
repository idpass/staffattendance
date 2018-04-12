package np.com.naxa.staffattendance.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;


import np.com.naxa.staffattendance.R;
import np.com.naxa.staffattendance.application.StaffAttendance;
import np.com.naxa.staffattendance.attendence.TeamMemberResposne;
import np.com.naxa.staffattendance.pojo.Staff;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by samir on 4/1/2018.
 */

public class StaffDao {

    private long saveStaff(ContentValues contentValues) {
        return DatabaseHelper.getDatabaseHelper().getWritableDatabase().insert(DatabaseHelper.TABLE_STAFF, null, contentValues);
    }

    private long saveStaff(SQLiteDatabase database, ContentValues contentValues) {
        return database.replace(DatabaseHelper.TABLE_STAFF, null, contentValues);
    }

    public long saveStaff(TeamMemberResposne staff) {
        return saveStaff(getContentValuesFronSaff(staff));
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


    public ContentValues getContentValuesFronSaff(TeamMemberResposne staff) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.KEY_STAFF_FULL_NAME, staff.getFirstName().concat(" ").concat(staff.getLastName()));
        contentValues.put(DatabaseHelper.KEY_STAFF_TYPE, StaffAttendance.getStaffAttendance().getString(R.string.not_avaliable, "Staff type"));
        contentValues.put(DatabaseHelper.KEY_ID, staff.getId());
        contentValues.put(DatabaseHelper.KEY_STAFF_TEAM_ID, staff.getTeamID());
        contentValues.put(DatabaseHelper.KEY_STAFF_TEAM_NAME, staff.getTeamName());
        return contentValues;
    }


    public ArrayList<Staff> getStaffFromCursor(Cursor cursor) {
        ArrayList<Staff> staffs = new ArrayList<>();

        if (cursor == null || cursor.getCount() <= 0) {
            return staffs;
        }

        while (cursor.moveToNext()) {
            String id = DatabaseHelper.getStringFromCursor(cursor, DatabaseHelper.KEY_STAFF_TYPE);
            String name = DatabaseHelper.getStringFromCursor(cursor, DatabaseHelper.KEY_STAFF_FULL_NAME);
            Staff staff = new Staff(id, name);
            staffs.add(staff);
        }

        return staffs;
    }

}
