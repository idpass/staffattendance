package np.com.naxa.staffattendance.attendence;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;

import com.evernote.android.job.JobRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import np.com.naxa.staffattendance.R;
import np.com.naxa.staffattendance.data.TokenMananger;
import np.com.naxa.staffattendance.database.AttendanceDao;
import np.com.naxa.staffattendance.database.NewStaffDao;
import np.com.naxa.staffattendance.jobs.StaffAttendanceSyncJob;
import np.com.naxa.staffattendance.jobs.StaffDownloadJob;
import np.com.naxa.staffattendance.jobs.SyncHistoryActivity;
import np.com.naxa.staffattendance.login.LoginActivity;
import np.com.naxa.staffattendance.newstaff.NewStaffActivity;
import np.com.naxa.staffattendance.newstaff.NewStaffCall;
import np.com.naxa.staffattendance.pojo.NewStaffPojo;
import np.com.naxa.staffattendance.pojo.NewStaffPojoBuilder;
import np.com.naxa.staffattendance.utlils.DialogFactory;
import np.com.naxa.staffattendance.utlils.NetworkUtils;
import np.com.naxa.staffattendance.utlils.ToastUtils;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
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
        syncStaffAttendancePeriodic();
        syncStaffListPeriodic();

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
                LoginActivity.start(AttendanceViewPagerActivity.this);
                finish();
                break;
            case R.id.main_menu_refresh:
                if (NetworkUtils.isInternetAvailable()) {
                    uploadNewStaffThenRefreshStaff();
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

    private void uploadNewStaffThenRefreshStaff() {
        showPleaseWaitDialog();

        final NewStaffCall newStaffCall = new NewStaffCall();
        final NewStaffDao newStaffDao = new NewStaffDao();
        final AttendanceDao attendanceDao= new AttendanceDao();
        ArrayList<NewStaffPojo> newStaffs = new NewStaffDao().getOfflineStaffs();


        Observable
                .just(newStaffs)
                .flatMap(new Func1<ArrayList<NewStaffPojo>, Observable<ArrayList<NewStaffPojo>>>() {
                    @Override
                    public Observable<ArrayList<NewStaffPojo>> call(ArrayList<NewStaffPojo> newStaffs) {

                        if (newStaffs.isEmpty()) {
                            uploadAllFinalizedAttendance();
                            repository.fetchMyTeam()
                                    .subscribe(new Observer<Object>() {
                                        @Override
                                        public void onCompleted() {

                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            e.printStackTrace();
                                        }

                                        @Override
                                        public void onNext(Object o) {

                                        }
                                    });
                        }

                        return Observable.just(newStaffs);
                    }
                })
                .flatMapIterable(new Func1<ArrayList<NewStaffPojo>, Iterable<NewStaffPojo>>() {
                    @Override
                    public Iterable<NewStaffPojo> call(ArrayList<NewStaffPojo> newStaffPojos) {
                        return newStaffPojos;
                    }
                })
                .flatMap(new Func1<NewStaffPojo, Observable<NewStaffPojo>>() {
                    @Override
                    public Observable<NewStaffPojo> call(final NewStaffPojo newStaffPojo) {//old id

                        final File photoToUpload = null;

                        return newStaffCall.newStaffObservable(newStaffPojo, photoToUpload)
                                .flatMap(new Func1<NewStaffPojo, Observable<NewStaffPojo>>() {
                                    @Override
                                    public Observable<NewStaffPojo> call(NewStaffPojo newStaffPojoResponse) {//new id
                                        //todo change ids from server
                                        newStaffDao.deleteStaffById(String.valueOf(newStaffPojo.getId()));
                                        if(newStaffPojo.getId()!=newStaffPojoResponse.getId()){
                                            List<Pair<Integer, String>> pairList = attendanceDao.getAllUnfinilizedAttendanceListInPair();
                                            for(Pair<Integer, String> pair: pairList){

                                            }
                                        }

                                        return Observable.just(newStaffPojoResponse);
                                    }
                                });
                    }
                })
                .flatMap(new Func1<NewStaffPojo, Observable<?>>() {
                    @Override
                    public Observable<?> call(NewStaffPojo newStaffPojo) {
                        uploadAllFinalizedAttendance();
                        return repository.fetchMyTeam();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onCompleted() {
                        AttendanceViewPagerActivity.start(AttendanceViewPagerActivity.this, true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        closePleaseWaitDialog();
                        DialogFactory.createGenericErrorDialog(AttendanceViewPagerActivity.this,
                                "Failed to refresh Reason " + e.getMessage())
                                .show();
                    }

                    @Override
                    public void onNext(Object o) {

                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        closePleaseWaitDialog();
    }

    private void showPleaseWaitDialog() {
        dialog = DialogFactory.createProgressDialogHorizontal(AttendanceViewPagerActivity.this, "Please Wait");
        if (!dialog.isShowing()) {
            dialog.show();
        }
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
                .setPeriodic(JobRequest.MIN_INTERVAL, JobRequest.MIN_FLEX)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .build()
                .schedule();
    }

}
