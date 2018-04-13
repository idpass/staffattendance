package np.com.naxa.staffattendance;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import np.com.naxa.staffattendance.attendence.MyTeamRepository;
import np.com.naxa.staffattendance.attendence.TeamMemberResposne;
import np.com.naxa.staffattendance.database.StaffDao;
import np.com.naxa.staffattendance.database.TeamDao;
import np.com.naxa.staffattendance.pojo.Staff;
import rx.Observable;
import rx.functions.Func1;

public class DailyAttendanceFragment extends Fragment implements StaffListAdapter.OnStaffItemClickListener {

    private RecyclerView recyclerView;
    private StaffListAdapter stafflistAdapter;
    private TeamDao teamDao;
    private StaffDao staffDao;
    private FloatingActionButton fabUploadAttedance;
    private List<String> attedanceIds;

    public DailyAttendanceFragment() {

    }

    public void setAttedanceIds(List<String> attedanceIds) {
        this.attedanceIds = attedanceIds;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_daily_attendence, container, false);
        teamDao = new TeamDao();
        staffDao = new StaffDao();

        bindUI(rootView);
        setupRecyclerView();

        fabUploadAttedance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<TeamMemberResposne> stafflist = stafflistAdapter.getSelected();
                String teamId = teamDao.getOneTeamIdForDemo();
                Date cDate = new Date();
                String fDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cDate);

                new MyTeamRepository().uploadAttendance(teamId, fDate, stafflist);

            }
        });

        return rootView;
    }


    private void setupRecyclerView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        String teamId = teamDao.getOneTeamIdForDemo();

        List<TeamMemberResposne> staffs = new StaffDao().getStaffByTeamId(teamId);

        stafflistAdapter = new StaffListAdapter(getActivity(), staffs, attedanceIds, this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(stafflistAdapter);


    }

    private void bindUI(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_staff_list);
        fabUploadAttedance = getActivity().findViewById(R.id.fab_attedance);
    }

    @Override
    public void onStaffClick(int pos) {
        stafflistAdapter.toggleSelection(pos);

    }

    @Override
    public void onStaffLongClick(int pos) {

    }
}
