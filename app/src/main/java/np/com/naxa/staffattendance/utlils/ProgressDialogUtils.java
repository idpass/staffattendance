package np.com.naxa.staffattendance.utlils;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;

public class ProgressDialogUtils {

    public ProgressDialog getProgressDialog(Context context, @NonNull String msg) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setIndeterminate(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle(msg);
        return dialog;
    }
}
