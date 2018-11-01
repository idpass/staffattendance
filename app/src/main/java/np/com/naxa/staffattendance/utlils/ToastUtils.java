package np.com.naxa.staffattendance.utlils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

import np.com.naxa.staffattendance.application.StaffAttendance;

public class ToastUtils {

    @SuppressLint("StaticFieldLeak")
    static Context context = StaffAttendance.getStaffAttendance();

    public static void showShort(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showLong(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void showShort(@StringRes int id) {
        showShort(context.getString(id));
    }
}
