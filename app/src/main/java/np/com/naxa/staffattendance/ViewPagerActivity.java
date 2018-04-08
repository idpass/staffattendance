package np.com.naxa.staffattendance;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class ViewPagerActivity extends AppCompatActivity {

    private ViewPager viewPager;

    public class YoFragmentPagerAdapter extends FragmentPagerAdapter {
        public YoFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;

            fragment= new SomeFragment();

            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pager_viewpager);

        initUI();
    }

    private void initUI() {
        viewPager = findViewById(R.id.veiw_pager);

        viewPager.setAdapter(new YoFragmentPagerAdapter(getSupportFragmentManager()));
    }

}
