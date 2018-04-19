package np.com.naxa.staffattendance.application;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

import np.com.naxa.staffattendance.jobs.AttedanceUploadJob;

public class DataSyncJobCreator implements JobCreator {
    @Override
    @Nullable
    public Job create(@NonNull String tag) {
        switch (tag) {
            case AttedanceUploadJob.TAG:
                return new AttedanceUploadJob();
            default:
                return null;
        }
    }
}
