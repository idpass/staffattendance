package np.com.naxa.staffattendance.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import np.com.naxa.staffattendance.application.StaffAttendance;

public class SoftKeyboardUtils {

    private SoftKeyboardUtils() {
    }


    public static void hideSoftKeyboard(@NonNull View view) {
        getInputMethodManager().hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private static InputMethodManager getInputMethodManager() {
        return (InputMethodManager) StaffAttendance.getStaffAttendance().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

}
