package np.com.naxa.staffattendance.application;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.evernote.android.job.JobManager;
import com.facebook.stetho.Stetho;

import io.fabric.sdk.android.Fabric;
import np.com.naxa.staffattendance.jobs.DataSyncJobCreator;
import timber.log.Timber;

/**
 * Created by samir on 4/1/2018.
 */

public class StaffAttendance extends Application {
    public static StaffAttendance staffAttendance;
    public Boolean allowManualPresence = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        staffAttendance = this;
        Stetho.initializeWithDefaults(this);
        Timber.plant(new Timber.DebugTree());
        JobManager.create(this).addJobCreator(new DataSyncJobCreator());

    }

    public static StaffAttendance getStaffAttendance(){
        return staffAttendance ;
    }
}
