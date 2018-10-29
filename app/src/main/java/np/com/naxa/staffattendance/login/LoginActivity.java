package np.com.naxa.staffattendance.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import np.com.naxa.staffattendance.BuildConfig;
import np.com.naxa.staffattendance.FormCall;
import np.com.naxa.staffattendance.SharedPreferenceUtils;
import np.com.naxa.staffattendance.TeamRemoteSource;
import np.com.naxa.staffattendance.attendence.AttendanceViewPagerActivity;
import np.com.naxa.staffattendance.attendence.MyTeamRepository;
import np.com.naxa.staffattendance.data.APIClient;
import np.com.naxa.staffattendance.R;
import np.com.naxa.staffattendance.data.ApiInterface;
import np.com.naxa.staffattendance.data.TokenMananger;
import np.com.naxa.staffattendance.database.TeamDao;
import np.com.naxa.staffattendance.newstaff.NewStaffActivity;
import np.com.naxa.staffattendance.utlils.DialogFactory;
import np.com.naxa.staffattendance.utlils.ProgressDialogUtils;
import np.com.naxa.staffattendance.utlils.ToastUtils;
import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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

        getWindow().setBackgroundDrawableResource(R.drawable.login_background);
        if (TokenMananger.doesTokenExist()) {
            AttendanceViewPagerActivity.start(this, false);
            finish();
        }

        myTeamRepository = new MyTeamRepository();
        initUI();


        if (true) {
            tvUserName.setText(LoginHelper.getUserName());
            tvPassword.setText(LoginHelper.getPWD());
            new Handler().postDelayed(() -> btnLogin.performClick(), 3000);

        }


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
        try {
            dialog.show();
        } catch (WindowManager.BadTokenException e) {
            //do nothing
        }


        new LoginCall().login(username, password, new LoginCall.LoginCallListener() {
            @Override
            public void onSuccess() {

                fetchMyTeam();
                getBanksAndDesignation();

            }

            @Override
            public void onError() {
                try {
                    dialog.dismiss();
//                    ToastUtils.showShort("Login Error");
                } catch (WindowManager.BadTokenException e) {
                    //do nothing
                }

            }
        });
    }


    private void getBanksAndDesignation() {
        final Gson gson = new Gson();
        FormCall formCall = new FormCall();

        formCall.getDesignation()
                .subscribe(new Observer<List<String>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<String> designationList) {
                        SharedPreferenceUtils
                                .saveToPrefs(LoginActivity.this, SharedPreferenceUtils.KEY.Designation,
                                        gson.toJson(designationList));


                    }
                });


        formCall.getBankList().subscribe(new Observer<List<String>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<String> bankList) {
                SharedPreferenceUtils
                        .saveToPrefs(LoginActivity.this, SharedPreferenceUtils.KEY.Bank,
                                gson.toJson(bankList));

            }
        });
    }

    private void fetchMyTeam() {

        myTeamRepository.fetchMyTeam()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onCompleted() {
                        dialog.dismiss();

                        AttendanceViewPagerActivity.start(LoginActivity.this, false);
                        finish();


                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        TokenMananger.clearToken();
                        dialog.dismiss();
                        if (e instanceof HttpException) {
                            try {
                                HttpException httpException = (HttpException) e;
                                ResponseBody responseBody = httpException.response().errorBody();
                                switch (httpException.code()) {
                                    case 404:
                                        showErrorDialog("You are not assigned to a team yet");
                                        break;
                                    default:
                                        showErrorDialog(responseBody.string());
                                        break;
                                }

                            } catch (NullPointerException | IOException e1) {
                                showErrorDialog("");
                                e1.printStackTrace();
                            }
                        } else if (e instanceof SocketTimeoutException) {
                            showErrorDialog("Server took too long to respond");
                        } else if (e instanceof IOException) {
                            showErrorDialog(e.getMessage());
                        } else {
                            showErrorDialog(e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(Object o) {

                    }
                });
    }

    private void showErrorDialog(String message) {

        DialogFactory.createActionDialog(LoginActivity.this, "Failed to login", message)
                .setPositiveButton("Ok",null)
                  .show();

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
