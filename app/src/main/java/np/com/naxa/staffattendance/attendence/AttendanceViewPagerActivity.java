package np.com.naxa.staffattendance.attendence;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.evernote.android.job.JobRequest;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import np.com.naxa.staffattendance.R;
import np.com.naxa.staffattendance.SharedPreferenceUtils;
import np.com.naxa.staffattendance.TeamRemoteSource;
import np.com.naxa.staffattendance.data.APIClient;
import np.com.naxa.staffattendance.data.TokenMananger;
import np.com.naxa.staffattendance.database.AttendanceDao;
import np.com.naxa.staffattendance.database.DatabaseHelper;
import np.com.naxa.staffattendance.database.NewStaffDao;
import np.com.naxa.staffattendance.database.TeamDao;
import np.com.naxa.staffattendance.jobs.StaffAttendanceSyncJob;
import np.com.naxa.staffattendance.jobs.StaffDownloadJob;
import np.com.naxa.staffattendance.jobs.SyncHistoryActivity;
import np.com.naxa.staffattendance.login.LoginActivity;
import np.com.naxa.staffattendance.newstaff.NewStaffActivity;
import np.com.naxa.staffattendance.newstaff.NewStaffCall;
import np.com.naxa.staffattendance.pojo.NewStaffPojo;
import np.com.naxa.staffattendance.utlils.DialogFactory;
import np.com.naxa.staffattendance.utlils.NetworkUtils;
import np.com.naxa.staffattendance.utlils.ToastUtils;
import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
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
//        syncStaffAttendancePeriodic();
//        syncStaffListPeriodic();

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
                TokenMananger.clearToken();
                SharedPreferenceUtils.purge(getApplicationContext());
                DatabaseHelper.getDatabaseHelper().dropAll(DatabaseHelper.getDatabaseHelper().getWritableDatabase());
                LoginActivity.start(AttendanceViewPagerActivity.this);
                finish();
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


    private void uploadNewStaffThenRefresh() {

        final ArrayList<NewStaffPojo> newStaffs = new NewStaffDao().getOfflineStaffs();

        if (newStaffs.isEmpty()) {

            repository.fetchMyTeam()

                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            showPleaseWaitDialog();
                        }
                    })
                    .subscribe(new Observer<Object>() {
                        @Override
                        public void onCompleted() {

                            repository.bulkAttendanceUpload()
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe(new Subscriber<Object>() {
                                        @Override
                                        public void onCompleted() {
                                            closePleaseWaitDialog();
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            closePleaseWaitDialog();
                                            DialogFactory.createGenericErrorDialog(AttendanceViewPagerActivity.this,
                                                    e.getMessage())
                                                    .show();
                                        }

                                        @Override
                                        public void onNext(Object o) {
                                            closePleaseWaitDialog();
                                        }
                                    });
                        }

                        @Override
                        public void onError(Throwable e) {
                            closePleaseWaitDialog();
                            Crashlytics.logException(e);
                            DialogFactory.createGenericErrorDialog(AttendanceViewPagerActivity.this,
                                    e.getMessage())
                                    .show();
                        }

                        @Override
                        public void onNext(Object o) {

                        }
                    });


        } else {
            syncAttedanceWithOfflineStaff()
                    .doOnSubscribe(this::showPleaseWaitDialog)
                    .doOnTerminate(this::closePleaseWaitDialog)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Observer<Object>() {
                        @Override
                        public void onCompleted() {
                            repository.bulkAttendanceUpload()
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe(new Subscriber<Object>() {
                                        @Override
                                        public void onCompleted() {
                                            closePleaseWaitDialog();
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            closePleaseWaitDialog();
                                            DialogFactory.createGenericErrorDialog(AttendanceViewPagerActivity.this,
                                                    e.getMessage())
                                                    .show();
                                        }

                                        @Override
                                        public void onNext(Object o) {
                                            closePleaseWaitDialog();
                                        }
                                    });
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            Crashlytics.logException(e);
                            if (e instanceof HttpException) {
                                String msg = ((HttpException) e).message();
                                String code = String.valueOf(((HttpException) e).code());
                                DialogFactory.createDataSyncErrorDialog(AttendanceViewPagerActivity.this, msg, code).show();
                            } else {
                                DialogFactory
                                        .createGenericErrorDialog(AttendanceViewPagerActivity.this, e.getMessage())
                                        .show();
                            }
                        }

                        @Override
                        public void onNext(Object o) {

                        }

                    });
        }
    }

    private Observable<Object> syncAttedanceWithOfflineStaff() {

        final NewStaffCall newStaffCall = new NewStaffCall();
        final NewStaffDao newStaffDao = new NewStaffDao();
        final AttendanceDao attendanceDao = new AttendanceDao();
        final ArrayList<NewStaffPojo> newStaffs = new NewStaffDao().getOfflineStaffs();
        final String teamId = new TeamDao().getOneTeamIdForDemo();


        return Observable.just(newStaffs)
                .flatMapIterable(new Func1<ArrayList<NewStaffPojo>, Iterable<NewStaffPojo>>() {
                    @Override
                    public Iterable<NewStaffPojo> call(ArrayList<NewStaffPojo> offlineStaffs) {
                        return offlineStaffs;
                    }
                }).flatMap(new Func1<NewStaffPojo, Observable<Pair<String, String>>>() {
                    @Override
                    public Observable<Pair<String, String>> call(NewStaffPojo offlineStaff) {
                        String filePath = offlineStaff.getPhoto();
                        File file = null;
                        if (!TextUtils.isEmpty(filePath)) {
                            file = new File(filePath);
                        }

                        return newStaffCall.newStaffObservable(offlineStaff, file)
                                .map(new Func1<NewStaffPojo, Pair<String, String>>() {
                                    @Override
                                    public Pair<String, String> call(NewStaffPojo uploadedStaff) {
                                        newStaffDao.deleteStaffById(String.valueOf(offlineStaff.getId()));
                                        return Pair.create(offlineStaff.getId(), uploadedStaff.getId());
                                    }
                                });
                    }
                })
                .toList()
                .flatMap(new Func1<List<Pair<String, String>>, Observable<AttendanceResponse>>() {
                    @Override
                    public Observable<AttendanceResponse> call(List<Pair<String, String>> pairs) {
                        return attendanceDao.updateStaffIdObservable(pairs);
                    }
                }).flatMap(new Func1<AttendanceResponse, Observable<AttendanceResponse>>() {
                    @Override
                    public Observable<AttendanceResponse> call(AttendanceResponse attendanceResponse) {
                        return APIClient.getAPIService(getApplicationContext())
                                .postAttendanceForTeam(teamId,
                                        attendanceResponse.getAttendanceDate(false),
                                        attendanceResponse.getPresentStaffIds());
                    }
                }).flatMap(new Func1<AttendanceResponse, Observable<?>>() {
                    @Override
                    public Observable<?> call(AttendanceResponse attendanceResponse) {
                        attendanceDao.changeAttendanceStatus(attendanceResponse.getAttendanceDate(false), AttendanceDao.SyncStatus.UPLOADED);
                        return repository.fetchMyTeam();
                    }
                });
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

    private void uploadAllFinalizedAttendance() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showPleaseWaitDialog();
            }
        });

        repository.bulkAttendanceUpload()
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onCompleted() {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                closePleaseWaitDialog();
                                DialogFactory.createMessageDialog(AttendanceViewPagerActivity.this,
                                        "Attendance Uploaded",
                                        "All pending attendance has been uploaded")
                                        .show();
                            }
                        });
                    }

                    @Override
                    public void onError(final Throwable e) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                closePleaseWaitDialog();
                                DialogFactory.createGenericErrorDialog(AttendanceViewPagerActivity.this,
                                        "Failed to upload Reason " + e.getMessage())
                                        .show();
                            }
                        });
                    }

                    @Override
                    public void onNext(Object o) {

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

    private void syncStaffAttendancePeriodic() {
        staffAttedancelastJobId = new JobRequest.Builder(StaffAttendanceSyncJob.TAG)
                .setPeriodic(JobRequest.MIN_INTERVAL, JobRequest.MIN_FLEX)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .build()
                .schedule();
    }


    private void syncStaffListPeriodic() {
        staffListlastJobId = new JobRequest.Builder(StaffDownloadJob.TAG)
                .setPeriodic(TimeUnit.DAYS.toMillis(1), JobRequest.MIN_FLEX)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .build()
                .schedule();
    }

}
