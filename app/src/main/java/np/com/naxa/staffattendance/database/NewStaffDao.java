package np.com.naxa.staffattendance.database;

import android.content.ContentValues;

import np.com.naxa.staffattendance.pojo.NewStaffPojo;


public class NewStaffDao {

    public static String SAVED = "saved";
    public static String FAILED = "failed";
    public static String UPLOADED = "uploaded";


    public void saveNewStaff(NewStaffPojo newStaffPojo) {
        DatabaseHelper.getDatabaseHelper().getWritableDatabase().insert(DatabaseHelper.TABLE_NEW_STAFF, null, getContentValues(newStaffPojo));
    }

    private ContentValues getContentValues(NewStaffPojo pojo) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_STAFF_DESIGNATION, pojo.getDesignation());
        values.put(DatabaseHelper.KEY_STAFF_FIRST_NAME, pojo.getFirstName());
        values.put(DatabaseHelper.KEY_STAFF_LAST_NAME, pojo.getLastName());
        values.put(DatabaseHelper.KEY_STAFF_DOB, pojo.getDateOfBirth());
        values.put(DatabaseHelper.KEY_STAFF_GENDER, pojo.getGender());
        values.put(DatabaseHelper.KEY_STAFF_ETHNICITY, pojo.getEthnicity());
        values.put(DatabaseHelper.KEY_STAFF_BANK_ID, pojo.getBank());
        values.put(DatabaseHelper.KEY_STAFF_BANK_NAME, pojo.getBankName());
        values.put(DatabaseHelper.KEY_STAFF_ACCOUNT_NUMBER, pojo.getAccountNumber());
        values.put(DatabaseHelper.KEY_STAFF_CONTACT_NUMBER, pojo.getPhoneNumber());
        values.put(DatabaseHelper.KEY_STAFF_EMAIL, pojo.getEmail());
        values.put(DatabaseHelper.KEY_STAFF_ADDRESS, pojo.getAddress());
        values.put(DatabaseHelper.KEY_STAFF_CONTRACT_START_DATE, pojo.getContractStart());
        values.put(DatabaseHelper.KEY_STAFF_CONTRACT_END_DATE, pojo.getContractEnd());
        values.put(DatabaseHelper.KEY_STAFF_PHOTO, pojo.getPhoto());
        values.put(DatabaseHelper.KEY_STAFF_DETAIL_STATUS, pojo.getStatus());
        return values;
    }


}
