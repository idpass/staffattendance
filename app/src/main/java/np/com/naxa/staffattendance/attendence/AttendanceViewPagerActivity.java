package np.com.naxa.staffattendance.attendence;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Locale;

import np.com.naxa.staffattendance.R;
import np.com.naxa.staffattendance.SharedPreferenceUtils;
import np.com.naxa.staffattendance.TeamRemoteSource;
import np.com.naxa.staffattendance.data.TokenMananger;
import np.com.naxa.staffattendance.database.DatabaseHelper;
import np.com.naxa.staffattendance.jobs.SyncHistoryActivity;
import np.com.naxa.staffattendance.login.LoginActivity;
import np.com.naxa.staffattendance.newstaff.NewStaffActivity;
import np.com.naxa.staffattendance.utlils.DialogFactory;
import np.com.naxa.staffattendance.utlils.NetworkUtils;
import np.com.naxa.staffattendance.utlils.ToastUtils;
import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
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


    public static void start(Context context, boolean disableTransition) {
        Intent intent = new Intent(context, AttendanceViewPagerActivity.class);
        context.startActivity(intent);
        if (disableTransition) ((Activity) context).overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_attendence);
        repository = new MyTeamRepository();

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
                    String msg = "If you logout all your account data including";
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
                if (NetworkUtils.isInternetAvailable()) {
                    TeamRemoteSource.getInstance()
                            .syncAll()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(this::showPleaseWaitDialog)
                            .subscribe(new Observer<Object>() {
                                @Override
                                public void onCompleted() {
                                    closePleaseWaitDialog();
                                    DialogFactory.createSimpleOkErrorDialog(AttendanceViewPagerActivity.this, "Success", "Everything has been synced").show();
                                    Timber.i("onCompleted");
                                }

                                @Override
                                public void onError(Throwable e) {
                                    closePleaseWaitDialog();
                                    if (e instanceof HttpException) {
                                        try {
                                            ResponseBody responseBody = ((HttpException) e).response().errorBody();
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
                                public void onNext(Object o) {

                                }
                            });

                } else {
                    ToastUtils.showLong(getString(R.string.no_internet));
                }
                break;

            case R.id.main_menu_setting:
                SyncHistoryActivity.start(this);
//                    new AttendanceDao().getAllUnfinilizedAttendanceListInPair();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showErrorDialog(String message) {

        DialogFactory.createActionDialog(AttendanceViewPagerActivity.this, "Failed to sync", message)
                .setPositiveButton("Ok", (dialog, which) -> AttendanceViewPagerActivity.start(AttendanceViewPagerActivity.this, true))
                .show();

    }

    @Override
    protected void onPause() {
        super.onPause();
        runOnUiThread(this::closePleaseWaitDialog);

    }

    private void showPleaseWaitDialog() {
        runOnUiThread(() -> {
            dialog = DialogFactory.createProgressDialogHorizontal(AttendanceViewPagerActivity.this, "Please Wait");
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
