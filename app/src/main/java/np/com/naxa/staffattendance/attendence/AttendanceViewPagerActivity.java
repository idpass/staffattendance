package np.com.naxa.staffattendance.attendence;

import android.app.Activity;
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
import android.view.Menu;
import android.view.MenuItem;

import np.com.naxa.staffattendance.R;
import np.com.naxa.staffattendance.newstaff.NewStaffActivity;

public class AttendanceViewPagerActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tablayout;
    private AppBarLayout appbar;
    private ViewPager viewpager;
    private BottomNavigationView bottomNavigationView;

    public static void start(Context context, boolean disableTransition) {
        Intent intent = new Intent(context, AttendanceViewPagerActivity.class);
        context.startActivity(intent);
        if (disableTransition) ((Activity) context).overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_attendence);
        initView();
        setupViewPager();
        setupToolbar();

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




    private void setupViewPager() {
        tablayout.setupWithViewPager(viewpager);
        viewpager.setAdapter(new AttendanceViewPagerAdapter(getSupportFragmentManager()));
        viewpager.setCurrentItem(7, true);
    }


    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_general);
        tablayout = (TabLayout) findViewById(R.id.tab_layout);
        appbar = (AppBarLayout) findViewById(R.id.appbar_general);
        viewpager = (ViewPager) findViewById(R.id.veiw_pager);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

    }


}
