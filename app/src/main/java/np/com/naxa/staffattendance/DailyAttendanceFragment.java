package np.com.naxa.staffattendance;


import android.arch.lifecycle.LiveDataReactiveStreams;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import np.com.naxa.staffattendance.attendence.AttendanceResponse;
import np.com.naxa.staffattendance.attendence.AttendanceViewPagerActivity;
import np.com.naxa.staffattendance.attendence.MyTeamRepository;
import np.com.naxa.staffattendance.attendence.TeamMemberResposne;
import np.com.naxa.staffattendance.common.Constant;
import np.com.naxa.staffattendance.common.MessageEvent;
import np.com.naxa.staffattendance.database.AttendanceDao;
import np.com.naxa.staffattendance.database.StaffDao;
import np.com.naxa.staffattendance.database.TeamDao;
import np.com.naxa.staffattendance.pojo.Staff;
import np.com.naxa.staffattendance.pojo.StaffRepository;
import np.com.naxa.staffattendance.utlils.DateConvertor;
import np.com.naxa.staffattendance.utlils.DialogFactory;
import timber.log.Timber;

public class DailyAttendanceFragment extends Fragment implements StaffListAdapter.OnStaffItemClickListener {

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


    public DailyAttendanceFragment() {
        myTeamRepository = new MyTeamRepository();
        attedanceToUpload = new ArrayList<>();
    }

    public void setAttendanceIds(List<String> attendanceIds, String attendanceDate) {
        this.attedanceIds = attendanceIds;


        boolean isAttedanceEmpty = (attendanceIds == null) || attendanceIds.isEmpty();
        boolean isAttedanceDateToday = DateConvertor.getCurrentDate().equalsIgnoreCase(attendanceDate);
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


        return rootView;
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


                            ((AttendanceViewPagerActivity) getActivity()).geoTagHelper.start(DateConvertor.getCurrentDate());


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

        StaffRepository staffRepository = StaffRepository.getInstance();
        Publisher<List<Staff>> publisher = LiveDataReactiveStreams.toPublisher(getViewLifecycleOwner(),
                staffRepository.getAll(true));
        Observable.fromPublisher(publisher)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<List<Staff>>() {
                    @Override
                    public void onNext(List<Staff> staffs) {
                        if (staffs != null && staffs.size() > 0) {
                            stafflistAdapter = new StaffListAdapter(getActivity(), staffs, enablePersonSelection, attedanceIds, new StaffListAdapter.OnStaffItemClickListener() {
                                @Override
                                public void onStaffClick(int pos, Staff staff) {
                                    stafflistAdapter.toggleSelection(pos);

                                    if (attedanceToUpload.contains(staff.getId())) {
                                        Timber.i("Removing %s / %s", staff.getId(), staff.getFirstName());
                                        attedanceToUpload.remove(staff.getId());
                                    } else {
                                        Timber.i("Adding %s / %s", staff.getId(), staff.getFirstName());
                                        attedanceToUpload.add(staff.getId());
                                    }


                                    Timber.i("Current array is %s", attedanceToUpload.toString());
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
                            });
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

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    private void bindUI(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_staff_list);
        fabUploadAttedance = view.findViewById(R.id.fab_attedance);
        layoutNoData = view.findViewById(R.id.layout_no_data);
    }

    @Override
    public void onStaffClick(int pos, Staff staff) {
        stafflistAdapter.toggleSelection(pos);

        if (attedanceToUpload.contains(staff.getId())) {
            Timber.i("Removing %s / %s", staff.getId(), staff.getFirstName());
            attedanceToUpload.remove(staff.getId());
        } else {
            Timber.i("Adding %s / %s", staff.getId(), staff.getFirstName());
            attedanceToUpload.add(staff.getId());
        }


        Timber.i("Current array is %s", attedanceToUpload.toString());
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationCapturedEvent(MessageEvent event) {
        String attedanceId = event.getItem("id");
        String location = event.getMessage();
        String[] locationSplit = location.split(" ");

        String latitude = locationSplit[0];
        String longitude = locationSplit[1];
        String accurary = locationSplit[3];


    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

}
