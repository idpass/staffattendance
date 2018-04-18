package np.com.naxa.staffattendance;


import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import np.com.naxa.staffattendance.attendence.AttedanceResponse;
import np.com.naxa.staffattendance.attendence.AttendanceViewPagerActivity;
import np.com.naxa.staffattendance.attendence.MyTeamRepository;
import np.com.naxa.staffattendance.attendence.TeamMemberResposne;
import np.com.naxa.staffattendance.attendence.WeeklyAttendanceVPActivity;
import np.com.naxa.staffattendance.database.AttendanceDao;
import np.com.naxa.staffattendance.database.StaffDao;
import np.com.naxa.staffattendance.database.TeamDao;
import np.com.naxa.staffattendance.utlils.DateConvertor;
import np.com.naxa.staffattendance.utlils.DialogFactory;

public class DailyAttendanceFragment extends Fragment implements StaffListAdapter.OnStaffItemClickListener {

    private RecyclerView recyclerView;
    private StaffListAdapter stafflistAdapter;
    private TeamDao teamDao;
    private StaffDao staffDao;
    private FloatingActionButton fabUploadAttedance;
    private List<String> attedanceIds;
    private MyTeamRepository myTeamRepository;
    private boolean enablePersonSelection = false;


    public DailyAttendanceFragment() {
        myTeamRepository = new MyTeamRepository();
    }

    public void setAttendanceIds(List<String> attendanceIds) {
        this.attedanceIds = attendanceIds;
        if (attendanceIds == null || attendanceIds.isEmpty()) {
            enablePersonSelection = true;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_daily_attendence, container, false);
        teamDao = new TeamDao();
        staffDao = new StaffDao();

        bindUI(rootView);
        setupRecyclerView();

        setHasOptionsMenu(true);
        fabUploadAttedance.hide();
        fabUploadAttedance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showMarkPresentDialog();
                ArrayList<String> stafflist = stafflistAdapter.getSelectedStaffIds();

            }
        });

        return rootView;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_upload_attedance:


                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showMarkPresentDialog() {
        String title = "Mark selected as present?";
        String msg = "You won't be able to change this once confirmed.";

        DialogFactory.createActionDialog(getActivity(), title, msg)
                .setPositiveButton("Mark Present", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        //saving it offline
                        AttendanceDao attedanceDao = new AttendanceDao();
                        AttedanceResponse attedanceResponse = new AttedanceResponse();
                        attedanceResponse.setAttendanceDate(DateConvertor.getCurrentDate());
                        attedanceResponse.setStaffs(stafflistAdapter.getSelectedStaffID());
                        attedanceResponse.setDataSyncStatus(AttendanceDao.SyncStatus.FINALIZED);

                        ContentValues contentValues = attedanceDao.getContentValuesForAttedance(attedanceResponse);
                        attedanceDao.saveAttedance(contentValues);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AttendanceViewPagerActivity.start(getActivity(), true);
                            }
                        }, 1000);

                    }
                }).setNegativeButton("Dismiss", null).show();
    }


    private void setupRecyclerView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        String teamId = teamDao.getOneTeamIdForDemo();

        List<TeamMemberResposne> staffs = new StaffDao().getStaffByTeamId(teamId);

        stafflistAdapter = new StaffListAdapter(getActivity(), staffs, enablePersonSelection, attedanceIds, this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(stafflistAdapter);


    }

    private void bindUI(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_staff_list);
        fabUploadAttedance = getActivity().findViewById(R.id.fab_attedance);
    }

    @Override
    public void onStaffClick(int pos, TeamMemberResposne staff) {
        stafflistAdapter.toggleSelection(pos);
        ArrayList<TeamMemberResposne> stafflist = stafflistAdapter.getSelected();

        if (stafflistAdapter.getSelected().size() > 0) {
            fabUploadAttedance.show();
        } else {
            fabUploadAttedance.hide();
        }
    }

    @Override
    public void onStaffLongClick(int pos) {

    }
}
