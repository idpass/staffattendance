package np.com.naxa.staffattendance.attendence;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Locale;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import np.com.naxa.staffattendance.R;
import np.com.naxa.staffattendance.SharedPreferenceUtils;
import np.com.naxa.staffattendance.TeamRemoteSource;
import np.com.naxa.staffattendance.common.GeoTagHelper;
import np.com.naxa.staffattendance.common.network.ConnectionTest;
import np.com.naxa.staffattendance.data.TokenMananger;
import np.com.naxa.staffattendance.database.DatabaseHelper;
import np.com.naxa.staffattendance.jobs.SyncHistoryActivity;
import np.com.naxa.staffattendance.login.LoginActivity;
import np.com.naxa.staffattendance.newstaff.NewStaffActivity;
import np.com.naxa.staffattendance.utlils.DialogFactory;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import timber.log.Timber;


public class AttendanceViewPagerActivity extends AppCompatActivity {

    private static final String LAST_JOB_ID = "LAST_JOB_ID";
    private static final String LAST_JOB_ID_STAFF_LIST = "LAST_JOB_ID_STAFF_LIST";

    private Toolbar toolbar;
    private TabLayout tablayout;
    private AppBarLayout appbar;
    private ViewPager viewpager;
    private BottomNavigationView bottomNavigationView;
    private MyTeamRepository repository;
    private ProgressDialog dialog;
    private int staffAttedancelastJobId, staffListlastJobId;
    public GeoTagHelper geoTagHelper;


    public static void start(Context context, boolean disableTransition) {
        Intent intent = new Intent(context, AttendanceViewPagerActivity.class);
        context.startActivity(intent);
        if (disableTransition) ((Activity) context).overridePendingTransition(0, 0);
    }


    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(geoTagHelper.getLocationReceiver(), new IntentFilter("location_result"));
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_attendence);
        repository = new MyTeamRepository();
        geoTagHelper = new GeoTagHelper(this);

        initView();
        setupViewPager();
        setupToolbar();


        if (savedInstanceState != null) {
            staffAttedancelastJobId = savedInstanceState.getInt(LAST_JOB_ID, 0);
            staffListlastJobId = savedInstanceState.getInt(LAST_JOB_ID_STAFF_LIST, 0);
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_add_staff:
                        NewStaffActivity.start(AttendanceViewPagerActivity.this, false);
                        finish();
                        break;
                    case R.id.action_attedance:
                        break;
                }
                return true;
            }
        });
    }

    private void runSync() {
        TeamRemoteSource.getInstance()
                .syncAll(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        showPleaseWaitDialog();
                    }

                    @Override
                    public void onNext(Object o) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        closePleaseWaitDialog();
                        if (e instanceof HttpException) {
                            try {
                                ResponseBody responseBody = ((HttpException) e).response().errorBody();
                                showErrorDialog(responseBody.string());
                            } catch (NullPointerException | IOException e1) {
                                showErrorDialog("");
                                e1.printStackTrace();
                            }
                        } else if (e instanceof SocketTimeoutException | e instanceof ConnectTimeoutException | e instanceof SocketException) {
                            showTimeoutDialog();
//                            showErrorDialog("Server took too long to respond, perhaps internet is slower than usual");
                        } else {
                            showErrorDialog(e.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {
                        closePleaseWaitDialog();
                        DialogFactory.createSimpleOkErrorDialog(AttendanceViewPagerActivity.this, "Success", "Everything has been synced").show();
                        Timber.i("onCompleted");
                    }
                });
//                .doOnSubscribe(new Consumer<Disposable>() {
//                    @Override
//                    public void accept(Disposable disposable) throws Exception {
//                        showPleaseWaitDialog();
//                    }
//                })
//                .subscribe(new Observer<Object>() {
//                    @Override
//                    public void onError(Throwable e) {
//                        e.printStackTrace();
//                        closePleaseWaitDialog();
//                        if (e instanceof HttpException) {
//                            try {
//                                ResponseBody responseBody = ((HttpException) e).response().errorBody();
//                                showErrorDialog(responseBody.string());
//                            } catch (NullPointerException | IOException e1) {
//                                showErrorDialog("");
//                                e1.printStackTrace();
//                            }
//                        } else if (e instanceof SocketTimeoutException | e instanceof ConnectTimeoutException | e instanceof SocketException) {
//                            showTimeoutDialog();
////                            showErrorDialog("Server took too long to respond, perhaps internet is slower than usual");
//                        } else {
//                            showErrorDialog(e.getMessage());
//                        }
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        closePleaseWaitDialog();
//                        DialogFactory.createSimpleOkErrorDialog(AttendanceViewPagerActivity.this, "Success", "Everything has been synced").show();
//                        Timber.i("onCompleted");
//                    }
//
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(Object o) {
//                    }
//                });
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_people_white_24dp);

        getSupportActionBar().setTitle(R.string.toolbar_title_attedance);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(LAST_JOB_ID, staffAttedancelastJobId);
        outState.putInt(LAST_JOB_ID_STAFF_LIST, staffListlastJobId);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_logout:

                int a = DatabaseHelper.getDatabaseHelper().getNewStaffCount(DatabaseHelper.getDatabaseHelper().getWritableDatabase());
                int b = DatabaseHelper.getDatabaseHelper().getFinalizedCount(DatabaseHelper.getDatabaseHelper().getWritableDatabase());

                if ((a + b) == 0) {
                    TokenMananger.clearToken();
                    SharedPreferenceUtils.purge(getApplicationContext());
                    DatabaseHelper.getDatabaseHelper().delteAllRows(DatabaseHelper.getDatabaseHelper().getWritableDatabase());
                    LoginActivity.start(AttendanceViewPagerActivity.this);
                    finish();
                } else {
                    String msg = "If you logout all your account data including ";
                    if (a > 0) msg += String.format(Locale.US, "%d un-synced staff(s)", a);
                    if (b > 0) msg += String.format(Locale.US, "\n %d finalized attendance(s)", b);
                    msg += "\nwill be deleted.";

                    DialogFactory.createActionDialog(this, "Caution", msg)
                            .setPositiveButton("Delete and logout", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    TokenMananger.clearToken();
                                    SharedPreferenceUtils.purge(getApplicationContext());
                                    DatabaseHelper.getDatabaseHelper().delteAllRows(DatabaseHelper.getDatabaseHelper().getWritableDatabase());
                                    LoginActivity.start(AttendanceViewPagerActivity.this);
                                    finish();
                                }
                            })
                            .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .create()
                            .show();
                }
                break;

            case R.id.main_menu_refresh:
                runSync();
                break;

            case R.id.main_menu_setting:
                SyncHistoryActivity.start(this);
//                    new AttendanceDao().getAllUnfinilizedAttendanceListInPair();
                break;
        }
        return super.

                onOptionsItemSelected(item);

    }


    private void runSpeedTest() {
        ProgressDialog networkDialog = DialogFactory.createProgressDialogHorizontal(this, "Estimating network quality");
        android.support.v7.app.AlertDialog resultDialog = DialogFactory.createMessageDialog(this, "Network quality results", "");

        ConnectionTest.getINSTANCE().download(new ConnectionTest.ConnectionTestCallback() {
            @Override
            public void networkQuality(ConnectionTest.NetworkSpeed networkSpeed) {
                String message = "";
                switch (networkSpeed) {
                    case POOR:
                        message = "Poor network quality detected";
                        break;
                    case GOOD:
                        message = "Good network quality detected";
                        break;
                    case AVERAGE:
                        message = "Average network quality detected";
                        break;
                    case UNKNOWN:
                        message = "Network quality unknown";
                        break;
                }
                String finalMessage = message;
                runOnUiThread(() -> resultDialog.setMessage(finalMessage));

            }

            @Override
            public void onStart() {
                runOnUiThread(networkDialog::show);

            }

            @Override
            public void onEnd() {
                runOnUiThread(() -> {
                    networkDialog.dismiss();
                    resultDialog.show();
                });

            }

            @Override
            public void message(String message) {
                runOnUiThread(() -> {
                    resultDialog.setMessage(message);
                });

            }

        });
    }

    private void showErrorDialog(String message) {

        DialogFactory.createActionDialog(AttendanceViewPagerActivity.this, "Failed to sync", message)
                .setPositiveButton("Ok", (dialog, which) -> AttendanceViewPagerActivity.start(AttendanceViewPagerActivity.this, true))
                .show();

    }

    private void showTimeoutDialog() {
        DialogFactory.createActionDialog(AttendanceViewPagerActivity.this, "Slow Internet", "This took longer than expected, perhaps internet is slower than usual?")
                .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        runSync();
                    }
                })
                .setNegativeButton("Dismiss", null)
                .setNeutralButton("Run Speed test", (dialog, which) -> {
                    runSpeedTest();
                }).show();
    }


    @Override
    protected void onPause() {
        super.onPause();
        runOnUiThread(this::closePleaseWaitDialog);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(geoTagHelper.getLocationReceiver());

    }

    private void showPleaseWaitDialog() {
        runOnUiThread(() -> {
            dialog = DialogFactory.createProgressDialogHorizontal(AttendanceViewPagerActivity.this, "Syncing");

            if (!dialog.isShowing()) {
                dialog.show();
            }
        });

    }

    private void closePleaseWaitDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void setupViewPager() {
        tablayout.setupWithViewPager(viewpager);
        viewpager.setAdapter(new AttendanceViewPagerAdapter(getSupportFragmentManager()));
        viewpager.setCurrentItem(AttendanceViewPagerAdapter.TOTAL_NO_OF_DAYS, true);
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_general);
        tablayout = (TabLayout) findViewById(R.id.tab_layout);
        appbar = (AppBarLayout) findViewById(R.id.appbar_general);
        viewpager = (ViewPager) findViewById(R.id.veiw_pager);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
    }
}
