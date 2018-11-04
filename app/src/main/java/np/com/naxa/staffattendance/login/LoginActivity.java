package np.com.naxa.staffattendance.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import np.com.naxa.staffattendance.BuildConfig;
import np.com.naxa.staffattendance.FormCall;
import np.com.naxa.staffattendance.R;
import np.com.naxa.staffattendance.attendence.AttendanceViewPagerActivity;
import np.com.naxa.staffattendance.attendence.MyTeamRepository;
import np.com.naxa.staffattendance.data.APIClient;
import np.com.naxa.staffattendance.data.ApiInterface;
import np.com.naxa.staffattendance.data.MyTeamResponse;
import np.com.naxa.staffattendance.data.TokenMananger;
import np.com.naxa.staffattendance.utlils.DialogFactory;
import np.com.naxa.staffattendance.utlils.ToastUtils;
import okhttp3.ResponseBody;
import retrofit2.HttpException;


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


        if (BuildConfig.DEBUG) {
            tvUserName.setText(LoginHelper.getUserName());
            tvPassword.setText(LoginHelper.getPWD());
//            new Handler().postDelayed(() -> btnLogin.performClick(), 3000);

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

        Observable<Object> login = APIClient.getUploadClient()
                .create(ApiInterface.class)
                .getLoginDetailsObservable(username, password)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<LoginResponse, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(LoginResponse loginResponse) throws Exception {
                        TokenMananger.saveToken(loginResponse.getToken());
                        APIClient.removeRetrofitClient();

                        return APIClient.getUploadClient()
                                .create(ApiInterface.class)
                                .getMyTeam()
                                .subscribeOn(Schedulers.io())
                                .map(new Function<ArrayList<MyTeamResponse>, Object>() {
                                    @Override
                                    public Object apply(ArrayList<MyTeamResponse> myTeamResponses) throws Exception {
                                        if (myTeamResponses.isEmpty()) {
                                            TokenMananger.clearToken();
                                            throw new RuntimeException("You are not assigned to a team yet");
                                        }
                                        return Observable.empty();
                                    }
                                });
                    }
                });

        login.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onError(Throwable e) {
                        dialog.dismiss();
                        if (e instanceof HttpException) {
                            try {
                                HttpException httpException = (HttpException) e;
                                ResponseBody responseBody = httpException.response().errorBody();
                                showErrorDialog(responseBody.string());
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
                    public void onComplete() {
                        APIClient.removeRetrofitClient();
                        getBanksAndDesignation();
                        fetchMyTeam();
                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {

                    }
                });

    }


    private void getBanksAndDesignation() {
        final Gson gson = new Gson();
        FormCall formCall = new FormCall();

        formCall.getDesignation().subscribe(new Observer<ArrayList<ArrayList<String>>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ArrayList<ArrayList<String>> arrayLists) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        formCall.getBankList().subscribe(new Observer<List<String>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<String> strings) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });


    }

    private void fetchMyTeam() {

        myTeamRepository.fetchMyTeam()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        dialog.dismiss();

                        AttendanceViewPagerActivity.start(LoginActivity.this, false);
                        finish();
                    }
                });

        myTeamRepository.fetchMyTeam()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();

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
                    public void onComplete() {

                    }
                });
    }

    private void showErrorDialog(String message) {

        DialogFactory.createActionDialog(LoginActivity.this, "Failed to login", message)
                .setPositiveButton("Ok", null)
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

    public static void startNonActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }
}
