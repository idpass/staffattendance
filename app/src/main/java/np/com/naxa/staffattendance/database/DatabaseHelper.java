package np.com.naxa.staffattendance.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import np.com.naxa.staffattendance.application.StaffAttendance;

/**
 * Created by samir on 4/1/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static DatabaseHelper databaseHelper;

    // Logcat tag
    public static final String LOG = "DatabaseHelper";

    // Database Version
    public static final int DATABASE_VERSION = 2;

    // Database Name
    public static final String DATABASE_NAME = "staffManager2";

    // Table Names
    public static final String TABLE_STAFF = "staff_table";
    public static final String TABLE_NEW_STAFF = "new_staff_table";
    public static final String TABLE_ATTENDANCE = "attendance_table";

    // Common column names
    public static final String KEY_ID = "id";
    public static final String KEY_CREATED_AT = "created_date";
    public static final String KEY_UPDATED_AT = "updated_date";

    // Staff column names
    public static final String KEY_STAFF_FULL_NAME = "full_name";
    public static final String KEY_STAFF_TEAM_NAME = "team_name";
    public static final String KEY_STAFF_TEAM_ID = "team_id";
    public static final String KEY_STAFF_TYPE = "staff_type";
    public static final String KEY_CREATED_BY = "created_by";

    //New Staff Column names
    public static final String KEY_STAFF_DESIGNATION = "designation";
    public static final String KEY_STAFF_FIRST_NAME = "first_name";
    public static final String KEY_STAFF_LAST_NAME = "last_name";
    public static final String KEY_STAFF_DOB = "dob";
    public static final String KEY_STAFF_GENDER = "gender";
    public static final String KEY_STAFF_ETHNICITY = "ethnicity";
    public static final String KEY_STAFF_BANK = "bank";
    public static final String KEY_STAFF_ACCOUNT_NUMBER = "account_number";
    public static final String KEY_STAFF_CONTACT_NUMBER = "contact_number";
    public static final String KEY_STAFF_EMAIL = "email";
    public static final String KEY_STAFF_ADDRESS = "address";
    public static final String KEY_STAFF_CONTRACT_START_DATE = "contract_start_date";
    public static final String KEY_STAFF_CONTRACT_END_DATE = "contract_end_date";
    public static final String KEY_STAFF_PHOTO = "photo";
    public static final String KEY_STAFF_BANK_OTHER = "bank_other";
    public static final String KEY_STAFF_DETAIL_STATUS = "status";

    // Attendance column names
    public static final String KEY_ATTENDACE_DATE = "date";
    public static final String KEY_STAFFS_IDS = "staffs_ids";
    public static final String KEY_SUBMITTED_BY = "submitted_by";


    // Table Create Statements
    // staff table create statement
    private static final String CREATE_TABLE_STAFF = "CREATE TABLE " +
            TABLE_STAFF +
            "(" +
            KEY_ID + " INTEGER PRIMARY KEY," +
            KEY_STAFF_TEAM_ID + " TEXT," +//todo if team will have more attr than name and id, make new team table
            KEY_STAFF_TEAM_NAME + " TEXT," +
            KEY_STAFF_FULL_NAME + " TEXT," +
            KEY_STAFF_TYPE + " INTEGER," +
            KEY_CREATED_BY + " INTEGER," +
            KEY_CREATED_AT + " DATETIME," +
            KEY_UPDATED_AT + " DATETIME" +
            ")";

    // Attendance table create statement
    private static final String CREATE_TABLE_ATTENDANCE = "CREATE TABLE " +
            TABLE_ATTENDANCE +
            "(" +
            KEY_ID + " INTEGER PRIMARY KEY," +
            KEY_ATTENDACE_DATE + " DATETIME," +
            KEY_STAFFS_IDS + " TEXT," +
            KEY_SUBMITTED_BY + " INTEGER," +
            KEY_CREATED_AT + " DATETIME" + "," +
            KEY_UPDATED_AT + " DATETIME" +
            ")";

    //New staff table create statement
    private static final String CREATE_TABLE_NEW_STAFF = "CREATE TABLE " +
            TABLE_NEW_STAFF+
            "(" +
            KEY_ID + " INTEGER PRIMARY KEY," +
            KEY_STAFF_DESIGNATION + " TEXT," +
            KEY_STAFF_FIRST_NAME + " TEXT," +
            KEY_STAFF_LAST_NAME + " TEXT," +
            KEY_STAFF_DOB + " TEXT," +
            KEY_STAFF_GENDER + " TEXT," +
            KEY_STAFF_ETHNICITY + " TEXT," +
            KEY_STAFF_BANK + " TEXT," +
            KEY_STAFF_ACCOUNT_NUMBER + " TEXT," +
            KEY_STAFF_CONTACT_NUMBER + " TEXT," +
            KEY_STAFF_EMAIL + " TEXT," +
            KEY_STAFF_ADDRESS + " TEXT," +
            KEY_STAFF_CONTRACT_START_DATE + " TEXT," +
            KEY_STAFF_CONTRACT_END_DATE + " TEXT," +
            KEY_STAFF_PHOTO + " TEXT," +
            KEY_STAFF_BANK_OTHER + " TEXT," +
            KEY_STAFF_DETAIL_STATUS + " TEXT," +
            KEY_CREATED_AT + " DATETIME," +
            KEY_UPDATED_AT + " DATETIME" +
            ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    public static synchronized DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            return new DatabaseHelper(StaffAttendance.getStaffAttendance());
        }
        return databaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_STAFF);
        db.execSQL(CREATE_TABLE_NEW_STAFF);
        db.execSQL(CREATE_TABLE_ATTENDANCE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STAFF);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEW_STAFF);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTENDANCE);

        // create new tables
        onCreate(db);
    }


    public static String getStringFromCursor(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndex(columnName));
    }
}