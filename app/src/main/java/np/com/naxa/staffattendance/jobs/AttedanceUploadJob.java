package np.com.naxa.staffattendance.jobs;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;

public class AttedanceUploadJob extends Job {
    public static final String TAG = "job_demo_tag";

    @Override
    @NonNull
    protected Result onRunJob(Params params) {
        // run your job here


        return Result.SUCCESS;
    }

    private int scheduleAdvancedJob() {
        PersistableBundleCompat extras = new PersistableBundleCompat();
        extras.putString("key", "Hello world");

        int jobId = new JobRequest.Builder(AttedanceUploadJob.TAG)
                .setExecutionWindow(30_000L, 40_000L)
                .setBackoffCriteria(5_000L, JobRequest.BackoffPolicy.EXPONENTIAL)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .setExtras(extras)
                .setRequirementsEnforced(true)
                .setUpdateCurrent(true)
                .build()
                .schedule();

        return jobId;
    }

    private void runJobImmediately() {
        int jobId = new JobRequest.Builder(AttedanceUploadJob.TAG)
                .startNow()
                .build()
                .schedule();
    }

    private void cancelJob(int jobId) {
        JobManager.instance().cancel(jobId);
    }
}
