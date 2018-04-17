package np.com.naxa.staffattendance.attendence;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import np.com.naxa.staffattendance.DailyAttendanceFragment;
import np.com.naxa.staffattendance.R;
import np.com.naxa.staffattendance.database.AttendanceDao;
import np.com.naxa.staffattendance.database.TeamDao;
import np.com.naxa.staffattendance.login.LoginActivity;
import np.com.naxa.staffattendance.newstaff.NewStaffActivity;
import np.com.naxa.staffattendance.utlils.DateConvertor;
import np.com.naxa.staffattendance.utlils.ToastUtils;


public class WeeklyAttendanceVPActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private AttendanceDao attendanceDao;
    private MyTeamRepository myTeamRepository;
    private BottomNavigationView bottomNavigationView;


    public static void start(Context context, boolean disableTransition) {
        Intent intent = new Intent(context, WeeklyAttendanceVPActivity.class);
        context.startActivity(intent);
        if (disableTransition) ((Activity) context).overridePendingTransition(0, 0);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, WeeklyAttendanceVPActivity.class);
        context.startActivity(intent);
    }

    private ArrayList<AttedanceResponse> getAcessedAttedance() {
        String teamID = new TeamDao().getOneTeamIdForDemo();
        return attendanceDao.getAttendanceSheetForTeam(teamID);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_attendence);
        attendanceDao = new AttendanceDao();
        bindUI();
        setuptoolbar();
        ArrayList<AttedanceResponse> attedanceResponseArrayList = getAcessedAttedance();
        myTeamRepository = new MyTeamRepository();
        bottomNavigationView.setOnNavigationItemSelectedListener(this);


        List<AttedanceResponse> todaysAttedanceSheet = attendanceDao.getTodaysAddedance("");
        if (todaysAttedanceSheet != null && (todaysAttedanceSheet.size() == 1 || todaysAttedanceSheet.size() > 0)) {
            //not implemented
        } else {
            attedanceResponseArrayList.add(new AttedanceResponse(DateConvertor.getCurrentDate(), new ArrayList<String>()));
        }

        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(new YoFragmentPagerAdapter(getSupportFragmentManager(), attedanceResponseArrayList));

        final int scrollPostion = attedanceResponseArrayList.size();
        viewPager.setCurrentItem(scrollPostion, true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_refresh:
                myTeamRepository.fetchMyTeam();
                break;
        }

        return super.onOptionsItemSelected(item);

    }

    private void setuptoolbar() {

        setSupportActionBar(toolbar);
    }

    private void bindUI() {
        viewPager = findViewById(R.id.veiw_pager);
        tabLayout = findViewById(R.id.tab_layout);
        toolbar = findViewById(R.id.toolbar_general);
        bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_staff:
                NewStaffActivity.start(this, false);
                finish();
                break;

            case R.id.action_attedance:

                break;


        }
        return true;
    }

    public class YoFragmentPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<AttedanceResponse> attedanceResponses;

        YoFragmentPagerAdapter(FragmentManager fm, ArrayList<AttedanceResponse> attedanceResponses) {
            super(fm);
            this.attedanceResponses = attedanceResponses;
        }

        @Override
        public Fragment getItem(int position) {
            DailyAttendanceFragment fragment = null;
            AttedanceResponse attedance = attedanceResponses.get(position);


            fragment = new DailyAttendanceFragment();
            fragment.setAttedanceIds(attedance.getStaffs());


            return fragment;
        }

        @Override
        public int getCount() {
            return attedanceResponses.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {


            return attedanceResponses.get(position).getAttendanceDate();

        }
    }
}
