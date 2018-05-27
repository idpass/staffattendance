package np.com.naxa.staffattendance.jobs;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import np.com.naxa.staffattendance.attendence.MyTeamRepository;
import rx.Observer;
import timber.log.Timber;

import static np.com.naxa.staffattendance.jobs.StaffAttendanceSyncJob.DATE_FORMAT;

public class StaffDownloadJob extends Job {

    public static final String TAG = "staff_download_job_tag";
    private Result result;

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        refreshTeam();
        saveSuccess(result == Result.SUCCESS);
        return result;
    }

    @Override
    protected void onReschedule(int newJobId) {
        super.onReschedule(newJobId);
    }

    private void refreshTeam() {

        new MyTeamRepository().fetchMyTeam().subscribe(new Observer<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                result = Result.FAILURE;

            }

            @Override
            public void onNext(Object o) {
                result = Result.SUCCESS;
            }
        });
    }

    private void saveSuccess(boolean success) {
        String text = DATE_FORMAT.format(new Date()) + "\t\t" + "Staff list" + "\t\t" + success + '\n';
        try {
            FileUtils.writeFile(getSuccessFile(), text, true);
        } catch (IOException e) {
            Timber.e(e.getMessage());
        }
    }

    private File getSuccessFile() {
        return new File(getContext().getCacheDir(), "success.txt");
    }
}
