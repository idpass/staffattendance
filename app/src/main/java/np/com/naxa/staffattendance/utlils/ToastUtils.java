package np.com.naxa.staffattendance.utlils;

import android.widget.Toast;

import np.com.naxa.staffattendance.application.StaffAttendance;

public class ToastUtils {
    public static void showShort(String msg){
        Toast.makeText(StaffAttendance.getStaffAttendance(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void showLong(String msg){
        Toast.makeText(StaffAttendance.getStaffAttendance(), msg, Toast.LENGTH_LONG).show();
    }
}
