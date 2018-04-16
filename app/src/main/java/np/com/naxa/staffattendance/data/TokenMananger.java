package np.com.naxa.staffattendance.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import np.com.naxa.staffattendance.application.StaffAttendance;

import static android.content.ContentValues.TAG;


/**
 * Created by nishon.tan on 11/29/2016.
 */

public class TokenMananger {
    private static final String TAG = "TokenManager";
    private static String token;
    private static SharedPreferences sharedPreferences;


    public static String getToken() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(StaffAttendance.getStaffAttendance());
        String rawtoken = sharedPreferences.getString("TOKEN", "");

        if (!TextUtils.isEmpty(rawtoken)) {
            rawtoken = "Token " + rawtoken;
        }
<<<<<<< HEAD

        Log.i("TokenMananger",rawtoken);
=======
>>>>>>> 8f98128bfcfa2932a7fe3c7ec5a164fc3404c326
        return rawtoken;
    }


    public static boolean doesTokenExist() {
        return !TokenMananger.getToken().isEmpty();
    }

    @SuppressLint("ApplySharedPref")
    public static void saveToken(String token) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(StaffAttendance.getStaffAttendance());
        sharedPreferences.edit().putString("TOKEN", token).commit();
    }
}

