package np.com.naxa.staffattendance.application;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by samir on 4/1/2018.
 */

public class StaffAttendance extends Application {
    public static StaffAttendance staffAttendance;

    @Override
    public void onCreate() {
        super.onCreate();
        staffAttendance = this;
        Stetho.initializeWithDefaults(this);

    }

    public static StaffAttendance getStaffAttendance(){
        return staffAttendance ;
    }
}
