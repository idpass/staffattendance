package np.com.naxa.staffattendance.jobs;

import android.content.Context;
import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.evernote.android.job.JobManager;


public class DataSyncJobCreator implements JobCreator {

    @Override
    public Job create(@NonNull String tag) {
        switch (tag) {
            case StaffAttendanceSyncJob.TAG:
                return new StaffAttendanceSyncJob();
            default:
                return null;
        }
    }

    public static final class AddReceiver extends AddJobCreatorReceiver {
        @Override
        protected void addJobCreator(@NonNull Context context, @NonNull JobManager manager) {
            // manager.addJobCreator(new DataSyncJobCreator());
        }
    }
}
