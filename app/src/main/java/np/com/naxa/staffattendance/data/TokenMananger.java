package np.com.naxa.staffattendance.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import np.com.naxa.staffattendance.application.StaffAttendance;

import static android.content.ContentValues.TAG;


/**
 * Created by nishon.tan on 11/29/2016.
 */

public class TokenMananger {
    private static final String TAG = "TokenManager";
    private static String token, rawtoken;
    static SharedPreferences sharedPreferences;

    public TokenMananger() {

    }

    public static String getToken() {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(StaffAttendance.getStaffAttendance());
        rawtoken = sharedPreferences.getString("TOKEN", "");

        token = "Token " + rawtoken;


//        Timber.i(token);
        Log.d(TAG, "getToken: "+token);
        return token;
    }


    public static void saveToken(String token){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(StaffAttendance.getStaffAttendance());
        sharedPreferences.edit().putString("TOKEN",token).apply();
    }
}

