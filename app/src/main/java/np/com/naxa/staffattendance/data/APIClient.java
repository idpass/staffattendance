package np.com.naxa.staffattendance.data;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.readystatesoftware.chuck.ChuckInterceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import np.com.naxa.staffattendance.application.StaffAttendance;
import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

public class APIClient {

    private static final String TAG = "APIClient";
    public static final String BASE_URL = "http://app.fieldsight.org/";

    private static Retrofit retrofit = null;
    private static OkHttpClient okHttpClient;
    private static RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

    public static ApiInterface getAPIService(Context context) {
        return APIClient.getUploadClient().create(ApiInterface.class);
    }

    public static void removeRetrofitClient() {
        retrofit = null;
    }

    public static Retrofit getUploadClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .client(createOkHttpClient())
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(rxAdapter)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }


    private static OkHttpClient createOkHttpClient() {

        Interceptor authorization = new Interceptor() {
            @Override
            public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
                Request request = chain.request().newBuilder()
                        .addHeader("Authorization", TokenMananger.getToken()).build();
                return chain.proceed(request);
            }
        };

        OkHttpClient.Builder okHttpClientBuidler = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .addInterceptor(new ChuckInterceptor(StaffAttendance.getStaffAttendance().getApplicationContext()))
                .connectTimeout(4, TimeUnit.MINUTES)
                .readTimeout(4, TimeUnit.MINUTES);


        if (TokenMananger.doesTokenExist()) {
            okHttpClientBuidler.addInterceptor(authorization);
        }
        return okHttpClientBuidler.build();
    }


}

