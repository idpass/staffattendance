package np.com.naxa.staffattendance.jobs;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.evernote.android.job.Job;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import np.com.naxa.staffattendance.R;
import np.com.naxa.staffattendance.attendence.AttendanceViewPagerActivity;
import np.com.naxa.staffattendance.attendence.MyTeamRepository;
import np.com.naxa.staffattendance.utlils.DialogFactory;
import rx.Observer;
import timber.log.Timber;


public class StaffAttendanceSyncJob extends Job {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault());

    public static final String TAG = "staff_attedance_job_tag";
    private Result result;

    @Override
    @NonNull
    protected Result onRunJob(@NonNull final Params params) {
        uploadAllFinalizedAttendance();
        saveSuccess(result == Result.SUCCESS);
        return result;


    }

    @Deprecated
    private void showNotification(Params params) {
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, new Intent(getContext(), AttendanceViewPagerActivity.class), 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(TAG, getContext().getString(R.string.attedance_sync_title), NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Data synchronization");
            Objects.requireNonNull(getContext().getSystemService(NotificationManager.class)).createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(getContext(), TAG)
                .setContentTitle(getContext().getString(R.string.attedance_sync_title))
                .setContentText("Sync ran, exact " + params.isExact() + " , periodic " + params.isPeriodic() + ", transient " + params.isTransient())
                .setAutoCancel(true)
                .setChannelId(TAG)
                .setSound(null)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setShowWhen(true)
                .setColor(Color.BLUE)
                .setLocalOnly(true)
                .build();

        NotificationManagerCompat.from(getContext()).notify(new Random().nextInt(), notification);

    }

    @Override
    protected void onReschedule(int newJobId) {
        super.onReschedule(newJobId);
    }


    private void uploadAllFinalizedAttendance() {


        new MyTeamRepository()
                .bulkAttendanceUpload()
                .subscribe(new Observer<Object>() {
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
        String text = DATE_FORMAT.format(new Date()) + "\t\t" + "Staff Attendance" + "\t\t" + success + '\n';
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
