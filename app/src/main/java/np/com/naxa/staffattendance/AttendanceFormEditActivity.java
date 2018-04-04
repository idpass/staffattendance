package np.com.naxa.staffattendance;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import np.com.naxa.staffattendance.POJO.AttendancePOJO;

public class AttendanceFormEditActivity extends AppCompatActivity {

    private List<AttendancePOJO> movieList = new ArrayList<>();
    private AttandanceStaffRecyclerAdapter mAdapter;


    @BindView(R.id.staff_list_recycler_view)
    RecyclerView staffListRecyclerView;
    @BindView(R.id.fab_update_staff_attendance)
    FloatingActionButton fabUpdateStaffAttendance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_form_edit);
        ButterKnife.bind(this);

        getAndSetDateToView();

        initToolbar();

        initRecyclerView();

    }

    private void initToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getAndSetDateToView());
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
//            actionBar.setHomeAsUpIndicator(R.color.colorAccent);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public String getAndSetDateToView() {

        final Calendar c = Calendar.getInstance();
        int yy = c.get(Calendar.YEAR);
        int mm = c.get(Calendar.MONTH);
        int dd = c.get(Calendar.DAY_OF_MONTH);

        // set current date into textview
        return (new StringBuilder()
                // Month is 0 based, just add 1
                .append(yy).append(" ").append("-").append(mm + 1).append("-")
                .append(dd)).toString();
    }

    public void initRecyclerView() {
        mAdapter = new AttandanceStaffRecyclerAdapter(movieList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        staffListRecyclerView.setLayoutManager(mLayoutManager);
        staffListRecyclerView.setItemAnimator(new DefaultItemAnimator());
        staffListRecyclerView.setAdapter(mAdapter);

        prepareMovieData();
    }


    private void prepareMovieData() {
        AttendancePOJO attendancePOJO = new AttendancePOJO("2018-4-1", "1", "Ram", "Hari");
        movieList.add(attendancePOJO);

        attendancePOJO = new AttendancePOJO("2018-4-1", "2", "Shyam", "Hari");
        movieList.add(attendancePOJO);

        attendancePOJO = new AttendancePOJO("2018-4-1", "3", "Hari", "Hari");
        movieList.add(attendancePOJO);

        attendancePOJO = new AttendancePOJO("2018-4-1", "4", "Sumit", "Hari");
        movieList.add(attendancePOJO);

        attendancePOJO = new AttendancePOJO("2018-4-1", "5", "Samir", "Hari");
        movieList.add(attendancePOJO);

        attendancePOJO = new AttendancePOJO("2018-4-1", "6", "Nishon", "Hari");
        movieList.add(attendancePOJO);

        attendancePOJO = new AttendancePOJO("2018-4-1", "7", "Aashis", "Hari");
        movieList.add(attendancePOJO);

        attendancePOJO = new AttendancePOJO("2018-4-1", "8", "Shree 1", "Hari");
        movieList.add(attendancePOJO);

        attendancePOJO = new AttendancePOJO("2018-4-1", "9", "Shree 2", "Hari");
        movieList.add(attendancePOJO);

        attendancePOJO = new AttendancePOJO("2018-4-1", "10", "Madan", "Hari");
        movieList.add(attendancePOJO);

        attendancePOJO = new AttendancePOJO("2018-4-1", "11", "Krishna", "Hari");
        movieList.add(attendancePOJO);


        mAdapter.notifyDataSetChanged();
    }


    @OnClick(R.id.fab_update_staff_attendance)
    public void onViewClicked() {
        Intent intent = new Intent(AttendanceFormEditActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
