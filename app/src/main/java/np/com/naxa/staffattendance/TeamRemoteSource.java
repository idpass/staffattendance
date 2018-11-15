package np.com.naxa.staffattendance;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import np.com.naxa.staffattendance.application.StaffAttendance;
import np.com.naxa.staffattendance.attendence.AttendanceResponse;
import np.com.naxa.staffattendance.attendence.TeamMemberResposne;
import np.com.naxa.staffattendance.common.Constant;
import np.com.naxa.staffattendance.data.APIClient;
import np.com.naxa.staffattendance.data.ApiInterface;
import np.com.naxa.staffattendance.data.MyTeamResponse;
import np.com.naxa.staffattendance.data.TokenMananger;
import np.com.naxa.staffattendance.database.AttendanceDao;
import np.com.naxa.staffattendance.database.DatabaseHelper;
import np.com.naxa.staffattendance.database.NewStaffDao;
import np.com.naxa.staffattendance.database.StaffDao;
import np.com.naxa.staffattendance.database.TeamDao;
import np.com.naxa.staffattendance.login.LoginActivity;
import np.com.naxa.staffattendance.newstaff.NewStaffCall;
import np.com.naxa.staffattendance.pojo.NewStaffPojo;
import np.com.naxa.staffattendance.pojo.Staff;
import np.com.naxa.staffattendance.pojo.StaffRepository;


public class TeamRemoteSource {


    private static TeamRemoteSource teamRemoteSource;

    public static TeamRemoteSource getInstance() {
        if (teamRemoteSource == null) {
            teamRemoteSource = new TeamRemoteSource();
        }

        return teamRemoteSource;
    }


    public Observable<Object> syncAll(LifecycleOwner owner) {
        final ApiInterface api = APIClient.getUploadClient()
                .create(ApiInterface.class);

        StaffRepository staffRepository = StaffRepository.getInstance();




        Observable<Object> uploadNewStaff2 = staffRepository.getStaffFromStatus(Constant.StaffStatus.SAVED)
                .toObservable()
                .flatMapIterable(new Function<List<Staff>, Iterable<Staff>>() {
                    @Override
                    public Iterable<Staff> apply(List<Staff> staffList) throws Exception {
                        return staffList;
                    }
                })
                .flatMap(new Function<Staff, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Staff staff) throws Exception {
                        String filePath = staff.getPhoto();
                        File file = null;
                        if (!TextUtils.isEmpty(filePath)) {
                            file = new File(filePath);
                            if (!file.exists()) {
                                file = null;
                            }
                        }
                        return staffRepository.upload(staff, file)
                                .subscribeOn(Schedulers.io())
                                .map(new Function<Staff, Pair<String, String>>() {
                                    @Override
                                    public Pair<String, String> apply(Staff uploadedStaff) throws Exception {
                                        staffRepository.deleteStaff(staff);
                                        Staff syncedStaff = uploadedStaff;
                                        syncedStaff.setStatus(Constant.StaffStatus.SYNCED);
                                        staffRepository.save(syncedStaff);
                                        return Pair.create(staff.getId(), uploadedStaff.getId());
                                    }
                                })
                                .toList()
                                .toObservable()
                                .flatMap(new Function<List<Pair<String, String>>, ObservableSource<?>>() {
                                    @Override
                                    public ObservableSource<?> apply(List<Pair<String, String>> pairs) throws Exception {
                                        return AttendanceDao.getInstance().updateStaffIdObservable(pairs);
                                    }
                                });

                    }
                });


        NewStaffCall newStaffCall = new NewStaffCall();
        ArrayList<NewStaffPojo> unsyncedStaffList = new NewStaffDao().getOfflineStaffs();
        Observable<Object> uploadNewStaff = Observable.just(unsyncedStaffList)
                .flatMapIterable((Function<ArrayList<NewStaffPojo>, Iterable<NewStaffPojo>>) newStaffPojos -> newStaffPojos)
                .flatMap(new Function<NewStaffPojo, Observable<?>>() {
                    @Override
                    public Observable<?> apply(NewStaffPojo newStaffPojo) {
                        String filePath = newStaffPojo.getPhoto();
                        File file = null;
                        if (!TextUtils.isEmpty(filePath)) {
                            file = new File(filePath);
                            if (!file.exists()) {
                                file = null;
                            }
                        }

                        return newStaffCall.newStaffObservable(newStaffPojo, file)
                                .map(new Function<NewStaffPojo, Pair<String, String>>() {
                                    @Override
                                    public Pair<String, String> apply(NewStaffPojo uploadedStaff) {
                                        NewStaffDao.getInstance().deleteStaffById(String.valueOf(newStaffPojo.getId()));
                                        return Pair.create(newStaffPojo.getId(), uploadedStaff.getId());
                                    }
                                })
                                .toList()
                                .toObservable()
                                .flatMap(new Function<List<Pair<String, String>>, Observable<AttendanceResponse>>() {
                                    @Override
                                    public Observable<AttendanceResponse> apply(List<Pair<String, String>> pairs) {
                                        return AttendanceDao.getInstance().updateStaffIdObservable(pairs);
                                    }
                                });
                    }
                });

        Observable<Object> teamlist = api.getMyTeam()
                .map(new Function<ArrayList<MyTeamResponse>, ArrayList<MyTeamResponse>>() {
                    @Override
                    public ArrayList<MyTeamResponse> apply(ArrayList<MyTeamResponse> myTeamResponses) {
                        if (myTeamResponses.isEmpty()) {
                            Context context = StaffAttendance.getStaffAttendance().getApplicationContext();
                            TokenMananger.clearToken();
                            SharedPreferenceUtils.purge(StaffAttendance.getStaffAttendance().getApplicationContext());
                            DatabaseHelper.getDatabaseHelper().delteAllRows(DatabaseHelper.getDatabaseHelper().getWritableDatabase());
                            LoginActivity.startNonActivity(context);

                        }
                        return myTeamResponses;
                    }
                })
                .flatMapIterable((Function<ArrayList<MyTeamResponse>, Iterable<MyTeamResponse>>) myTeamResponses -> myTeamResponses)
                .flatMap(new Function<MyTeamResponse, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(MyTeamResponse myTeamResponse) throws Exception {
                        return api.getTeamMember(myTeamResponse.getId())
                                .flatMapIterable(new Function<ArrayList<TeamMemberResposne>, Iterable<TeamMemberResposne>>() {
                                    @Override
                                    public Iterable<TeamMemberResposne> apply(ArrayList<TeamMemberResposne> teamMemberResposnes) throws Exception {
                                        return teamMemberResposnes;
                                    }
                                }).doOnNext(new Consumer<TeamMemberResposne>() {
                                    @Override
                                    public void accept(TeamMemberResposne teamMemberResposne) throws Exception {
                                        teamMemberResposne.setTeamID(myTeamResponse.getId());
                                        teamMemberResposne.setTeamName(myTeamResponse.getName());
                                        StaffDao.getInstance().saveStaff(teamMemberResposne);
                                    }
                                })
                                .doOnSubscribe(new Consumer<Disposable>() {
                                    @Override
                                    public void accept(Disposable disposable) throws Exception {
                                        StaffDao.getInstance().removeAllStaffList();
                                    }
                                })
                                .toList()
                                .toObservable();
                    }
                });

        Observable<Object> teamlist2 = api.getMyTeam()
                .map(new Function<ArrayList<MyTeamResponse>, ArrayList<MyTeamResponse>>() {
                    @Override
                    public ArrayList<MyTeamResponse> apply(ArrayList<MyTeamResponse> myTeamResponses) {
                        if (myTeamResponses.isEmpty()) {
                            Context context = StaffAttendance.getStaffAttendance().getApplicationContext();
                            TokenMananger.clearToken();
                            SharedPreferenceUtils.purge(StaffAttendance.getStaffAttendance().getApplicationContext());
                            DatabaseHelper.getDatabaseHelper().delteAllRows(DatabaseHelper.getDatabaseHelper().getWritableDatabase());
                            staffRepository.deleteAll();
                            LoginActivity.startNonActivity(context);
                        }
                        return myTeamResponses;
                    }
                })
                .flatMapIterable((Function<ArrayList<MyTeamResponse>, Iterable<MyTeamResponse>>) myTeamResponses -> myTeamResponses)
                .flatMap(new Function<MyTeamResponse, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(MyTeamResponse myTeamResponse) throws Exception {
                        return api.getTeamMember2(myTeamResponse.getId())
                                .flatMapIterable(new Function<ArrayList<Staff>, Iterable<Staff>>() {
                                    @Override
                                    public Iterable<Staff> apply(ArrayList<Staff> staffs) throws Exception {
                                        return staffs;
                                    }
                                }).doOnNext(new Consumer<Staff>() {
                                    @Override
                                    public void accept(Staff staff) throws Exception {
                                        staff.setTeamID(myTeamResponse.getId());
                                        staff.setTeamName(myTeamResponse.getName());
                                        staff.setStatus(Constant.StaffStatus.SYNCED);
                                        staffRepository.save(staff);
                                    }
                                })
                                .doOnSubscribe(new Consumer<Disposable>() {
                                    @Override
                                    public void accept(Disposable disposable) throws Exception {
                                        staffRepository.deleteAll();
                                    }
                                })
                                .toList()
                                .toObservable();
                    }
                });

        Observable<Object> pastAttendance = api.getMyTeam()
                .flatMapIterable((Function<ArrayList<MyTeamResponse>, Iterable<MyTeamResponse>>) myTeamResponses -> myTeamResponses)
                .flatMap(new Function<MyTeamResponse, ObservableSource<ArrayList<AttendanceResponse>>>() {
                    @Override
                    public ObservableSource<ArrayList<AttendanceResponse>> apply(MyTeamResponse myTeamResponse) throws Exception {
                        return api.getPastAttendanceList(myTeamResponse.getId());
                    }
                }).flatMap(new Function<ArrayList<AttendanceResponse>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(ArrayList<AttendanceResponse> attendanceResponses) throws Exception {
                        AttendanceDao.getInstance().removeAllAttedance();
                        return AttendanceDao.getInstance().saveAttendance(attendanceResponses);
                    }
                });


        final String teamId = new TeamDao().getOneTeamIdForDemo();
        ArrayList<AttendanceResponse> attendanceResponses = AttendanceDao.getInstance().getFinalizedAttendanceSheet();


        Observable<AttendanceResponse> attendanceSheet = AttendanceDao.getInstance().getFinalizedAttedanceSheetObservable()
                .map(new Function<ArrayList<AttendanceResponse>, ArrayList<AttendanceResponse>>() {
                    @Override
                    public ArrayList<AttendanceResponse> apply(ArrayList<AttendanceResponse> attendanceResponses) {
                        if (TextUtils.isEmpty(teamId))
                            throw new RuntimeException("Team ID is missing");
                        return attendanceResponses;
                    }
                })
                .flatMapIterable((Function<ArrayList<AttendanceResponse>, Iterable<AttendanceResponse>>) attendanceRespons -> attendanceRespons)
                .flatMap((Function<AttendanceResponse, Observable<AttendanceResponse>>) attendanceResponse -> {

                    return api.postAttendanceForTeam(teamId,
                            attendanceResponse.getAttendanceDate(false),
                            attendanceResponse.getPresentStaffIds());
                });



        return Observable.concatArray(uploadNewStaff2, teamlist2, pastAttendance, attendanceSheet, pastAttendance);

//        return uploadNewStaff2;
//
    }


}
