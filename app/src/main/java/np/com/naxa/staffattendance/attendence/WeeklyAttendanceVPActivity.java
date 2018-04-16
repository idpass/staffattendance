package np.com.naxa.staffattendance.attendence;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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
import np.com.naxa.staffattendance.utlils.DateConvertor;
import np.com.naxa.staffattendance.utlils.ToastUtils;

public class WeeklyAttendanceVPActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private AttendanceDao attendanceDao;
    private MyTeamRepository myTeamRepository;


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

        List<AttedanceResponse> todaysAttedanceSheet = attendanceDao.getTodaysAddedance("");
        if (todaysAttedanceSheet != null && todaysAttedanceSheet.size() == 1) {

        } else if (todaysAttedanceSheet != null && todaysAttedanceSheet.size() > 0) {

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
            for (AttedanceResponse attedance : attedanceResponses) {
                fragment = new DailyAttendanceFragment();
                fragment.setAttedanceIds(attedance.getStaffs());
            }

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
