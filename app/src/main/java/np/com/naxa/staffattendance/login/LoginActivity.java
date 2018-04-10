package np.com.naxa.staffattendance.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import np.com.naxa.staffattendance.NewStaffActivity;
import np.com.naxa.staffattendance.R;
import np.com.naxa.staffattendance.WeeklyAttendenceVPActivity;
import np.com.naxa.staffattendance.data.APIClient;
import np.com.naxa.staffattendance.data.ApiInterface;
import np.com.naxa.staffattendance.data.LoginResponse;
import np.com.naxa.staffattendance.data.TokenMananger;
import np.com.naxa.staffattendance.utlils.ProgressDialogUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    private EditText tvUserName, tvPassword;
    private Button btnLogin;
    private TokenMananger tokenMananger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        tokenMananger = new TokenMananger();
        initUI();

        tvUserName.setText("arunb@unops.org");
        tvPassword.setText("arubhan");

    }

    private void initUI() {
        tvUserName = findViewById(R.id.tv_email);
        tvPassword = findViewById(R.id.tv_password);
        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                ProgressDialog dialog = new ProgressDialogUtils().getProgressDialog(this, "Signing in");
                if (validate()) {
                    dialog.show();
                    sendDataToServer(tvUserName.getText().toString(), tvPassword.getText().toString());
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Enter valid credentials..", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    public void sendDataToServer(String username, String password) {
        ApiInterface apiService = APIClient.getUploadClient().create(ApiInterface.class);

        Call<LoginResponse> call = apiService.getLoginDetails(username, password);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.body() != null) {
                    TokenMananger.saveToken(response.body().getToken());
                    Log.d(TAG, "onResponse: " + response.body().getToken());
                    startActivity(new Intent(LoginActivity.this, NewStaffActivity.class));
                } else {
                    Log.d(TAG, "onResponse: " + " null response");
                    Toast.makeText(LoginActivity.this, "Login failed!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Invalid Credentials.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private boolean validate() {
        boolean valid = true;
        boolean ck = false;

        String email = tvUserName.getText().toString();
        String password = tvPassword.getText().toString();

        if (email.isEmpty()) {
            tvUserName.requestFocus();
            tvUserName.setError("Enter a valid username or email address");
        } else {
            tvUserName.setError(null);
            ck = true;
        }

        if (password.isEmpty() || password.length() < 4) {
            if (ck) {
                tvPassword.requestFocus();
            }
            tvPassword.setError("Enter a valid password!!");
            valid = false;
        } else {
            tvPassword.setError(null);
        }

        return valid;
    }
}
