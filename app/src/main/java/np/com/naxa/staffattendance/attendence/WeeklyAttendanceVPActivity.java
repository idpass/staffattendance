package np.com.naxa.staffattendance.attendence;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.function.Predicate;

import np.com.naxa.staffattendance.DailyAttendanceFragment;
import np.com.naxa.staffattendance.R;
import np.com.naxa.staffattendance.database.AttendanceDao;
import np.com.naxa.staffattendance.database.TeamDao;
import np.com.naxa.staffattendance.utlils.DateConvertor;

public class WeeklyAttendanceVPActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private AttendanceDao attendanceDao;


    private ArrayList<AttedanceResponse> generateSevenDaysAttendanceSheet() {
        String teamID = new TeamDao().getOneTeamIdForDemo();
        ArrayList<AttedanceResponse> list = attendanceDao.getAttendanceSheetForTeam(teamID);
        return list;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_attendence);
        attendanceDao = new AttendanceDao();
        bindUI();
        setuptoolbar();
        ArrayList<AttedanceResponse> list = generateSevenDaysAttendanceSheet();

        //todo stop loading old attedance history
        //list.clear();

        if (attendanceDao.getTodaysAddedance("") != null) {
            list.addAll(attendanceDao.getTodaysAddedance(""));
        } else {
            list.add(new AttedanceResponse(DateConvertor.getCurrentDate(), new ArrayList<String>()));
        }
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(new YoFragmentPagerAdapter(getSupportFragmentManager(), list));
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

        public YoFragmentPagerAdapter(FragmentManager fm, ArrayList<AttedanceResponse> attedanceResponses) {
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
