package np.com.naxa.staffattendance.common.network;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


@SuppressLint("LogNotTimber")
public class ConnectionTest {
    private long startTime;
    private long endTime;
    private long fileSize;


    public static ConnectionTest INSTANCE;

    private OkHttpClient client = new OkHttpClient();

    // bandwidth in kbps
    private int POOR_BANDWIDTH = 150;
    private int AVERAGE_BANDWIDTH = 550;
    private int GOOD_BANDWIDTH = 2000;

    private String TAG = this.getClass().getSimpleName();

    public static ConnectionTest getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new ConnectionTest();
        }

        return INSTANCE;
    }

    public void download(ConnectionTestCallback callback) {
        startTime = System.currentTimeMillis();

        Request request = new Request.Builder()
                .url("https://fieldsight.s3.amazonaws.com/logo/Asia_P3_Hub.jpg")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                callback.networkQuality(NetworkSpeed.UNKNOWN);
            }


            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Headers responseHeaders = response.headers();
                for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                    Log.d(TAG, responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }

                try (InputStream input = response.body().byteStream()) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];

                    while (input.read(buffer) != -1) {
                        bos.write(buffer);
                    }
                    byte[] docBuffer = bos.toByteArray();
                    fileSize = bos.size();
                }catch (NullPointerException e){
                    e.printStackTrace();
                    callback.networkQuality(NetworkSpeed.UNKNOWN);
                }


                endTime = System.currentTimeMillis();

                // calculate how long it took by subtracting endtime from starttime

                double timeTakenMills = Math.floor(endTime - startTime);  // time taken in milliseconds
                double timeTakenSecs = timeTakenMills / 1000;  // divide by 1000 to get time in seconds
                final int kilobytePerSec = (int) Math.round(1024 / timeTakenSecs);

                if (kilobytePerSec <= POOR_BANDWIDTH) {
                    callback.networkQuality(NetworkSpeed.POOR);
                } else if (kilobytePerSec <= AVERAGE_BANDWIDTH) {
                    callback.networkQuality(NetworkSpeed.AVERAGE);
                } else if (kilobytePerSec >= GOOD_BANDWIDTH) {
                    callback.networkQuality(NetworkSpeed.AVERAGE);
                }


                // get the download speed by dividing the file size by time taken to download
                double speed = fileSize / timeTakenMills;

                Log.d(TAG, "Time taken in secs: " + timeTakenSecs);
                Log.d(TAG, "kilobyte per sec: " + kilobytePerSec);
                Log.d(TAG, "Download Speed: " + speed);
                Log.d(TAG, "File size: " + fileSize);

            }
        });
    }


    public interface ConnectionTestCallback {
        void networkQuality(NetworkSpeed networkSpeed);
    }

    enum NetworkSpeed {
        POOR,
        AVERAGE,
        GOOD,
        UNKNOWN
    }
}
