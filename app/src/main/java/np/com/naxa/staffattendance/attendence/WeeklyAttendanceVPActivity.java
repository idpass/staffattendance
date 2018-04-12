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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import np.com.naxa.staffattendance.DailyAttendanceFragment;
import np.com.naxa.staffattendance.R;

public class WeeklyAttendanceVPActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;

    public class YoFragmentPagerAdapter extends FragmentPagerAdapter {
        public YoFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            fragment = new DailyAttendanceFragment();
            return fragment;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String date = new SimpleDateFormat("yyyy-MM-", Locale.ENGLISH).format(new Date());
            int day = Integer
                    .parseInt(
                            new SimpleDateFormat("dd", Locale.ENGLISH).format(new Date())) - (7 - position);
            return date + day;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_attendence);
        bindUI();
        setuptoolbar();
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(new YoFragmentPagerAdapter(getSupportFragmentManager()));
    }

    private void setuptoolbar() {

        setSupportActionBar(toolbar);
    }

    private void bindUI() {
        viewPager = findViewById(R.id.veiw_pager);
        tabLayout = findViewById(R.id.tab_layout);
        toolbar = findViewById(R.id.toolbar_general);

    }
}
