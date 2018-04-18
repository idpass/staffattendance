package np.com.naxa.staffattendance.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import np.com.naxa.staffattendance.attendence.AttendanceViewPagerActivity;
import np.com.naxa.staffattendance.attendence.MyTeamRepository;
import np.com.naxa.staffattendance.data.APIClient;
import np.com.naxa.staffattendance.R;
import np.com.naxa.staffattendance.data.TokenMananger;
import np.com.naxa.staffattendance.utlils.DialogFactory;
import np.com.naxa.staffattendance.utlils.ProgressDialogUtils;
import np.com.naxa.staffattendance.utlils.ToastUtils;
import rx.Observer;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    private EditText tvUserName, tvPassword;
    private Button btnLogin;
    private TokenMananger tokenMananger;
    private MyTeamRepository myTeamRepository;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);


        if (TokenMananger.doesTokenExist()) {
            AttendanceViewPagerActivity.start(this, false);
            finish();
        }

        myTeamRepository = new MyTeamRepository();
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
                dialog = DialogFactory.createProgressDialogHorizontal(this, getString(R.string.msg_please_wait));
                if (validate()) {

                    loginToServer(tvUserName.getText().toString(), tvPassword.getText().toString());

                } else {
                    ToastUtils.showShort("Enter valid credentials..");
                }
                break;
        }
    }


    private void loginToServer(final String username, final String password) {
        dialog.show();
        new LoginCall().login(username, password, new LoginCall.LoginCallListener() {
            @Override
            public void onSuccess() {
                APIClient.removeRetrofitClient();
                fetchMyTeam();

            }

            @Override
            public void onError() {
                dialog.dismiss();
                ToastUtils.showShort("Login Error");
            }
        });
    }

    private void fetchMyTeam() {

        myTeamRepository.fetchMyTeam().subscribe(new Observer<Object>() {
            @Override
            public void onCompleted() {
                dialog.dismiss();
                AttendanceViewPagerActivity.start(LoginActivity.this, false);

            }

            @Override
            public void onError(Throwable e) {
                ToastUtils.showLong(e.getMessage());
            }

            @Override
            public void onNext(Object o) {

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

    public static void start(Context attendanceViewPagerActivity) {
        Intent intent = new Intent(attendanceViewPagerActivity, LoginActivity.class);
        attendanceViewPagerActivity.startActivity(intent);
    }
}
