package np.com.naxa.staffattendance.application;

import android.app.Application;

import com.facebook.stetho.Stetho;

import timber.log.Timber;

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
        Timber.plant(new Timber.DebugTree());

    }

    public static StaffAttendance getStaffAttendance(){
        return staffAttendance ;
    }
}
