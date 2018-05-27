package np.com.naxa.staffattendance.jobs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

import np.com.naxa.staffattendance.R;


public class SyncHistoryActivity extends Activity {

    private TextView tvSyncLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_history);

        tvSyncLog = (TextView) findViewById(R.id.textView_log);
        refreshView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_sync_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sync_now:
                ;
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void refreshView() {
        tvSyncLog.setText(getSuccessHistory());
    }

    @NonNull
    public String getSuccessHistory() {
        try {
            byte[] data = FileUtils.readFile(getSuccessFile());
            if (data == null || data.length == 0) {
                return "";
            }
            return new String(data);
        } catch (IOException e) {
            return "";
        }
    }

    private File getSuccessFile() {
        return new File(getApplicationContext().getCacheDir(), "success.txt");
    }

    public static void start(Context attendanceViewPagerActivity) {
        Intent intent = new Intent(attendanceViewPagerActivity,SyncHistoryActivity.class);
        attendanceViewPagerActivity.startActivity(intent);
    }
}
