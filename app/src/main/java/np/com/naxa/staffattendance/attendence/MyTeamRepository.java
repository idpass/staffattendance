package np.com.naxa.staffattendance.attendence;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import np.com.naxa.staffattendance.SharedPreferenceUtils;
import np.com.naxa.staffattendance.application.StaffAttendance;
import np.com.naxa.staffattendance.data.APIClient;
import np.com.naxa.staffattendance.data.ApiInterface;
import np.com.naxa.staffattendance.data.MyTeamResponse;
import np.com.naxa.staffattendance.database.AttendanceDao;
import np.com.naxa.staffattendance.database.StaffDao;
import np.com.naxa.staffattendance.database.TeamDao;


public class MyTeamRepository {

    private StaffDao staffDao;
    private AttendanceDao attendanceDao;


    public MyTeamRepository() {
        staffDao = new StaffDao();
        attendanceDao = new AttendanceDao();
    }


    public Observable<Object> fetchMyTeam() {
        final ApiInterface apiInterface = APIClient
                .getUploadClient()
                .create(ApiInterface.class);

        String teamId = SharedPreferenceUtils.getFromPrefs(StaffAttendance.getStaffAttendance(), SharedPreferenceUtils.KEY.TeamID, "");

        return myTeamObservable()
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        staffDao.removeAllStaffList();
                    }
                })
                .flatMap(new Function<List<TeamMemberResposne>, Observable<ArrayList<AttendanceResponse>>>() {
                    @Override
                    public Observable<ArrayList<AttendanceResponse>> apply(List<TeamMemberResposne> teamMemberResposnes) {
                        String teamId = SharedPreferenceUtils.getFromPrefs(StaffAttendance.getStaffAttendance(), SharedPreferenceUtils.KEY.TeamID, "");
                        staffDao.saveStafflist(teamMemberResposnes);
                        return apiInterface.getPastAttendanceList(teamId);
                    }
                })
                .flatMap(new Function<ArrayList<AttendanceResponse>, Observable<?>>() {
                    @Override
                    public Observable<?> apply(ArrayList<AttendanceResponse> attendanceRespons) {
                        attendanceDao.removeAllAttedance();
                        return attendanceDao.saveAttendance(attendanceRespons);
                    }
                });


    }

    private Observable<List<TeamMemberResposne>> myTeamObservable() {
        final ApiInterface apiInterface = APIClient.getUploadClient().create(ApiInterface.class);
        return apiInterface.getMyTeam()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapIterable(new Function<ArrayList<MyTeamResponse>, Iterable<MyTeamResponse>>() {
                    @Override
                    public Iterable<MyTeamResponse> apply(ArrayList<MyTeamResponse> myTeamResponses) {
                        return myTeamResponses;
                    }
                })
                .flatMap(new Function<MyTeamResponse, Observable<TeamMemberResposne>>() {
                    @Override
                    public Observable<TeamMemberResposne> apply(final MyTeamResponse myTeamResponse) {
                        final String teamId = myTeamResponse.getId();//get team id
                        SharedPreferenceUtils.saveToPrefs(StaffAttendance.getStaffAttendance().getApplicationContext(), SharedPreferenceUtils.KEY.TeamID, teamId);

                        return apiInterface.getTeamMember(teamId)
                                .subscribeOn(Schedulers.io())//request team memeber for each id
                                .flatMapIterable(new Function<ArrayList<TeamMemberResposne>, Iterable<TeamMemberResposne>>() {
                                    @Override
                                    public Iterable<TeamMemberResposne> apply(ArrayList<TeamMemberResposne> teamMemberResposnes) {
                                        return teamMemberResposnes;//make team response iterable (loopable)
                                    }
                                }).flatMap(new Function<TeamMemberResposne, Observable<TeamMemberResposne>>() {
                                    @Override
                                    public Observable<TeamMemberResposne> apply(TeamMemberResposne teamMemberResposne) {
                                        //add team id and team name is team member obj

                                        teamMemberResposne.setTeamID(teamId);
                                        teamMemberResposne.setTeamName(myTeamResponse.getName());
                                        return Observable.just(teamMemberResposne);
                                    }
                                });
                    }
                })
                .toList()
                .toObservable();

    }

    private Observable<Object> uploadAttendance(final String teamId, final String date, final ArrayList<TeamMemberResposne> stafflist) {
        final ApiInterface apiInterface = APIClient.getUploadClient().create(ApiInterface.class);

        return staffDao.getStaffIdFromObject(stafflist)
                .flatMap(new Function<List<String>, Observable<AttendanceResponse>>() {
                    @Override
                    public Observable<AttendanceResponse> apply(List<String> stafflist) {
                        return apiInterface.postAttendanceForTeam(teamId, date, stafflist);
                    }
                }).flatMap(new Function<AttendanceResponse, Observable<?>>() {
                    @Override
                    public Observable<?> apply(AttendanceResponse attendanceResponse) {
                        if (attendanceResponse != null) {
                            attendanceDao.updateAttendance(attendanceResponse.getAttendanceDate(false), teamId);
                        }
                        return null;
                    }
                });
    }

    public Observable<Object> bulkAttendanceUpload() {
        final ApiInterface apiInterface = APIClient.getUploadClient().create(ApiInterface.class);
        final String teamId = new TeamDao().getOneTeamIdForDemo();
        ArrayList<AttendanceResponse> attendanceSheet = attendanceDao.getFinalizedAttendanceSheet();

        return Observable.just(attendanceSheet)
                .flatMapIterable((Function<ArrayList<AttendanceResponse>, Iterable<AttendanceResponse>>) attendanceRespons -> attendanceRespons)
                .flatMap((Function<AttendanceResponse, Observable<AttendanceResponse>>) attendanceResponse -> {

                    return apiInterface.postAttendanceForTeam(teamId,
                            attendanceResponse.getAttendanceDate(false),
                            attendanceResponse.getPresentStaffIds());
                })
                .flatMap((Function<AttendanceResponse, Observable<AttendanceResponse>>) attendanceResponse -> {
                    if (attendanceResponse != null) {
                        attendanceDao.updateAttendance(attendanceResponse.getAttendanceDate(false), teamId);
                    }
                    return null;
                });
    }
}
