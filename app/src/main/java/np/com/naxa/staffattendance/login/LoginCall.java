package np.com.naxa.staffattendance.login;

import android.util.Log;

import np.com.naxa.staffattendance.data.APIClient;
import np.com.naxa.staffattendance.data.ApiInterface;
import np.com.naxa.staffattendance.data.TokenMananger;
import np.com.naxa.staffattendance.utlils.ToastUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginCall {

    private static final String TAG = "LoginActivity";

    public void login(final String username, final String password, final LoginCallListener listener) {
        ApiInterface apiService = APIClient.getUploadClient().create(ApiInterface.class);

        Call<LoginResponse> call = apiService.getLoginDetails(username, password);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.body() != null) {
                    TokenMananger.saveToken(response.body().getToken());
                    Log.d(TAG, "onResponse: " + response.body().getToken());
                    listener.onSuccess();

                } else {
                    listener.onError();
                    Log.d(TAG, "onResponse: " + " null response");

                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                ToastUtils.showShort("Invalid Credentials.");
                listener.onError();
            }
        });

    }


    public interface LoginCallListener {
        void onError();

        void onSuccess();
    }
}
