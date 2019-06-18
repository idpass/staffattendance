package np.com.naxa.staffattendance;


import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.idpass.mobile.api.IDPassConstants;
import org.idpass.mobile.api.IDPassIntent;
import org.idpass.mobile.proto.SignedAction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import np.com.naxa.staffattendance.application.StaffAttendance;
import np.com.naxa.staffattendance.attendence.AttendanceResponse;
import np.com.naxa.staffattendance.attendence.AttendanceViewPagerActivity;
import np.com.naxa.staffattendance.attendence.MyTeamRepository;
import np.com.naxa.staffattendance.attendence.TeamMemberResposne;
import np.com.naxa.staffattendance.database.AttendanceDao;
import np.com.naxa.staffattendance.database.DatabaseHelper;
import np.com.naxa.staffattendance.database.StaffDao;
import np.com.naxa.staffattendance.database.TeamDao;
import np.com.naxa.staffattendance.newstaff.NewStaffActivity;
import np.com.naxa.staffattendance.utlils.DateConvertor;
import np.com.naxa.staffattendance.utlils.DialogFactory;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import rx.Observable;
import timber.log.Timber;

public class DailyAttendanceFragment extends Fragment implements StaffListAdapter.OnStaffItemClickListener {
    private int IDENTIFY_RESULT_INTENT = 1;
    private NfcAdapter nfcAdapter;


    private RecyclerView recyclerView;
    private StaffListAdapter stafflistAdapter;
    private TeamDao teamDao;
    private StaffDao staffDao;
    private FloatingActionButton fabUploadAttedance;

    private List<String> attedanceIds;
    private MyTeamRepository myTeamRepository;
    private boolean enablePersonSelection = false;
    private List<String> attedanceToUpload;
    private RelativeLayout layoutNoData;
    private boolean isAttedanceDateToday = false;


    public DailyAttendanceFragment() {
    }

    public void setAttendanceIds(List<String> attendanceIds, String attendanceDate) {
        this.attedanceIds = attendanceIds;


        boolean isAttedanceEmpty = (attendanceIds == null) || attendanceIds.isEmpty();
        isAttedanceDateToday = DateConvertor.getCurrentDate().equalsIgnoreCase(attendanceDate);

        if (isAttedanceEmpty && isAttedanceDateToday) {
            enablePersonSelection = true;
        }

        boolean isAttendenceNotEmpty = !isAttedanceEmpty;

        if (isAttedanceDateToday && isAttendenceNotEmpty) {
            enablePersonSelection = false;
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_daily_attendence, container, false);

        myTeamRepository = new MyTeamRepository();
        attedanceToUpload = new ArrayList<>();
        teamDao = new TeamDao();
        staffDao = new StaffDao();


        bindUI(rootView);
        setupRecyclerView();

        setHasOptionsMenu(true);

        fabUploadAttedance.hide();
        fabUploadAttedance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        String peoplelist = TeamDao.getInstance().getTeamMembers(attedanceToUpload);
                        String title = "Mark selected as present?";
                        String msg = "%s.\n\nYou won't be able to change this once confirmed.";
                        msg = String.format(msg, peoplelist);


                        showMarkPresentDialog(title, msg);
                    }
                });
            }
        });

        if (enablePersonSelection && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            nfcAdapter = NfcAdapter.getDefaultAdapter(getContext());
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (enablePersonSelection && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            nfcAdapter.enableReaderMode(this.getActivity(), tag -> {
                    Intent intent = IDPassIntent.intentIdentify(IDPassConstants.IDPASS_TYPE_MIFARE, true, true);
                    intent.putExtra(NfcAdapter.EXTRA_TAG, tag);
                    startActivityForResult(intent, IDENTIFY_RESULT_INTENT);
                }
                , NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,null);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (enablePersonSelection && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            nfcAdapter.disableReaderMode(this.getActivity());
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IDENTIFY_RESULT_INTENT && resultCode == Activity.RESULT_OK) {
            String signedActionBase64 = data.getStringExtra(IDPassConstants.IDPASS_SIGNED_ACTION_RESULT_EXTRA);

            SignedAction signedAction = IDPassIntent.signedActionBuilder(signedActionBase64);

            String idPassDID = signedAction.getAction().getPerson().getDid();

            List<TeamMemberResposne> staffs = new StaffDao().getStaffByIdPassDID(idPassDID);
            if (staffs.size() > 0) {
                TeamMemberResposne staff = staffs.get(0);
                if (!attedanceToUpload.contains(staff.getId())) {
                    this.stafflistAdapter.markPresent(staff.getId());
                    this.attedanceToUpload.add(staff.getId());
                    fabUploadAttedance.show();
                }
            }
        }
    }


    private void showMarkPresentDialog(String title, String msg) {
        getActivity().runOnUiThread(() -> {
            DialogFactory.createActionDialog(getActivity(), title, msg)
                    .setPositiveButton("Mark Present", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {


                            //saving it offline
                            AttendanceDao attedanceDao = new AttendanceDao();


                            AttendanceResponse attendanceResponse = new AttendanceResponse();
                            attendanceResponse.setAttendanceDate(DateConvertor.getCurrentDate());
                            attendanceResponse.setStaffs(attedanceToUpload);
                            attendanceResponse.setDataSyncStatus(AttendanceDao.SyncStatus.FINALIZED);

                            ContentValues contentValues = attedanceDao.getContentValuesForAttedance(attendanceResponse);
                            attedanceDao.saveAttedance(contentValues);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    AttendanceViewPagerActivity.start(getActivity(), true);
                                }
                            }, 1000);

                        }
                    }).setNegativeButton("Dismiss", null).show();
        });

    }


    private void setupRecyclerView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        String teamId = teamDao.getOneTeamIdForDemo();

        List<TeamMemberResposne> staffs = new StaffDao().getStaffByTeamId(teamId);

        if (staffs != null && staffs.size() > 0) {
            stafflistAdapter = new StaffListAdapter(getActivity(), staffs, enablePersonSelection, attedanceIds, this);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(stafflistAdapter);

            recyclerView.setVisibility(View.VISIBLE);
            layoutNoData.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            layoutNoData.setVisibility(View.VISIBLE);
        }


    }

    private void bindUI(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_staff_list);
        fabUploadAttedance = view.findViewById(R.id.fab_attedance);
        layoutNoData = view.findViewById(R.id.layout_no_data);
    }

    @Override
    public void onStaffClick(int pos, TeamMemberResposne staff) {

        if (attedanceToUpload.contains(staff.getId())) {
            stafflistAdapter.toggleSelection(pos);
            Timber.i("Removing %s / %s", staff.getIDPassDID(), staff.getFirstName());
            attedanceToUpload.remove(staff.getId());
        } else {
            Timber.i("Adding %s / %s", staff.getIDPassDID(), staff.getFirstName());
            if (((StaffAttendance) Objects.requireNonNull(this.getContext()).getApplicationContext()).allowManualPresence) {
                stafflistAdapter.toggleSelection(pos);
                attedanceToUpload.add(staff.getId());
            }
        }

        if (stafflistAdapter.getSelected().size() > 0) {
            fabUploadAttedance.show();
        } else {
            fabUploadAttedance.hide();
        }
    }

    @Override
    public void onStaffLongClick(int pos) {
        Timber.i("Saving staffIds %s", attedanceToUpload.toString());
    }
}
