package np.com.naxa.staffattendance.login;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import np.com.naxa.staffattendance.R;
import np.com.naxa.staffattendance.data.APIClient;
import np.com.naxa.staffattendance.data.ApiInterface;
import np.com.naxa.staffattendance.data.LoginResponse;
import np.com.naxa.staffattendance.data.TokenMananger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    TextInputLayout tvUserName, tvPassword ;
    FloatingActionButton btnUserLoginFab;

    TokenMananger tokenMananger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tokenMananger = new TokenMananger();


        tvUserName = (TextInputLayout)findViewById(R.id.tv_email_or_username);
        tvPassword = (TextInputLayout)findViewById(R.id.tv_password);
        btnUserLoginFab = (FloatingActionButton)findViewById(R.id.fab_user_login);
        btnUserLoginFab.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        if(validateText(tvUserName) && validateText(tvPassword)){

            sendDataToServer(tvUserName.getEditText().getText().toString(), tvPassword.getEditText().getText().toString());
        }
    }


    public void sendDataToServer(String username, String password){
        ApiInterface apiService = APIClient.getClient(getApplicationContext()).create(ApiInterface.class);

        String uname = "arunb@unops.org";
        String upass = "arubhan";

        LoginBody loginBody = new LoginBody(uname, upass);
//        Call<LoginResponse> call = apiService.getLoginDetails(loginBody);
        Call<LoginResponse> call = apiService.getLoginDetails(username, upass);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.body() == null){
                    Log.d(TAG, "onResponse: "+" null response");
                    return;
                }
                Log.d(TAG, "onResponse: "+response.body().toString());

            }


            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

            }
        });
    }

    public boolean validateText(TextInputLayout textInputLayout){
        if(TextUtils.isEmpty(textInputLayout.getEditText().getText())){
            textInputLayout.getEditText().requestFocus();
            textInputLayout.setError("Field is required");
            return false;
        }
        return true;
    }
}
