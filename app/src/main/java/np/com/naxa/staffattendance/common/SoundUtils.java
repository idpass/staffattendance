package np.com.naxa.staffattendance.common;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import np.com.naxa.staffattendance.application.StaffAttendance;

public class SoundUtils {
    public static void playNotificationSound() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(StaffAttendance.getStaffAttendance(), notification);
            r.play();
        } catch (Exception e) {
            //ignored
        }
    }
}
