package np.com.naxa.staffattendance;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import np.com.naxa.staffattendance.application.StaffAttendance;
import np.com.naxa.staffattendance.attendence.Attendance;
import np.com.naxa.staffattendance.attendence.AttendanceRepository;
import np.com.naxa.staffattendance.attendence.AttendanceResponse;
import np.com.naxa.staffattendance.common.Constant;
import np.com.naxa.staffattendance.data.APIClient;
import np.com.naxa.staffattendance.data.ApiInterface;
import np.com.naxa.staffattendance.data.MyTeamResponse;
import np.com.naxa.staffattendance.data.TokenMananger;
import np.com.naxa.staffattendance.database.AttendanceDao;
import np.com.naxa.staffattendance.database.DatabaseHelper;
import np.com.naxa.staffattendance.database.TeamDao;
import np.com.naxa.staffattendance.login.LoginActivity;
import np.com.naxa.staffattendance.staff.Staff;
import np.com.naxa.staffattendance.staff.StaffRepository;


public class TeamRemoteSource {


    private static TeamRemoteSource teamRemoteSource;

    public static TeamRemoteSource getInstance() {
        if (teamRemoteSource == null) {
            teamRemoteSource = new TeamRemoteSource();
        }

        return teamRemoteSource;
    }


    public Observable<Object> syncAll() {
        final ApiInterface api = APIClient.getUploadClient()
                .create(ApiInterface.class);

        StaffRepository staffRepository = StaffRepository.getInstance();
        AttendanceRepository attendanceRepository = AttendanceRepository.getInstance();

        Observable<Object> uploadNewStaff = staffRepository.getStaffFromStatus(Constant.StaffStatus.SAVED)
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

        Observable<Object> teamlist = api.getMyTeam()
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
                .flatMapIterable(new Function<ArrayList<MyTeamResponse>, Iterable<MyTeamResponse>>() {
                    @Override
                    public Iterable<MyTeamResponse> apply(ArrayList<MyTeamResponse> myTeamResponses) throws Exception {
                        return myTeamResponses;
                    }
                })
                .flatMap(new Function<MyTeamResponse, ObservableSource<ArrayList<AttendanceResponse>>>() {
                    @Override
                    public ObservableSource<ArrayList<AttendanceResponse>> apply(MyTeamResponse myTeamResponse) throws Exception {
                        return api.getPastAttendanceList(myTeamResponse.getId());
                    }
                }).flatMap(new Function<ArrayList<AttendanceResponse>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(ArrayList<AttendanceResponse> attendanceResponses) throws Exception {
                        attendanceRepository.removeAll();
                        return attendanceRepository.save(attendanceResponses);
                    }
                });


        final String teamId = new TeamDao().getOneTeamIdForDemo();


        Observable<Object> attendanceSheet = attendanceRepository.getAttendanceByStatus(Constant.AttendanceStatus.FINALIZED)
                .toObservable()
                .map(new Function<List<Attendance>, List<Attendance>>() {
                    @Override

                    public List<Attendance> apply(List<Attendance> attendances) throws Exception {
                        if (TextUtils.isEmpty(teamId))
                            throw new RuntimeException("Team ID is missing");
                        return attendances;
                    }
                })
                .flatMapIterable(new Function<List<Attendance>, Iterable<Attendance>>() {
                    @Override
                    public Iterable<Attendance> apply(List<Attendance> attendances) throws Exception {
                        return attendances;
                    }
                })
                .flatMap(new Function<Attendance, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Attendance attendance) throws Exception {
                        String staffIDs = attendance.getStaffIds();
                        String[] staffIDlist = staffIDs.replace("[", "").replace("]", "").split(",");
                        return api.postAttendanceForTeam(teamId,
                                attendance.getAttendanceDate(),
                                Arrays.asList(staffIDlist));
                    }
                });

        return Observable.concatArray(uploadNewStaff, teamlist, attendanceSheet, pastAttendance);

    }


}
