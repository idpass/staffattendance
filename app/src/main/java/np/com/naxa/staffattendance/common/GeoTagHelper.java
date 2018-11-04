package np.com.naxa.staffattendance.common;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.lang.ref.WeakReference;

import timber.log.Timber;

public class GeoTagHelper {

    private WeakReference<Context> context;
    private String idToGeoTag;

    public GeoTagHelper(Activity activity) {
        context = new WeakReference<>(activity);
    }

    public void start(String idToGeoTag) {
        this.idToGeoTag = idToGeoTag;
        Intent intent = new Intent(context.get(), GeoPointForegroundService.class);
        intent.setAction(GeoPointForegroundService.ACTION_START_FOREGROUND_SERVICE);
        context.get().startService(intent);
    }

    private BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            Timber.i("Adding geotag(%s) for attendance on %s", message, idToGeoTag);

            updateAttedanceWithGeoTag(idToGeoTag);
        }
    };

    private void updateAttedanceWithGeoTag(String idToGeoTag) {

    }

    public BroadcastReceiver getLocationReceiver() {
        return locationReceiver;
    }

}
