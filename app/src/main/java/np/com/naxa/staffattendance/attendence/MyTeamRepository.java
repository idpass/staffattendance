package np.com.naxa.staffattendance.attendence;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import np.com.naxa.staffattendance.SharedPreferenceUtils;
import np.com.naxa.staffattendance.application.StaffAttendance;
import np.com.naxa.staffattendance.data.APIClient;
import np.com.naxa.staffattendance.data.ApiInterface;
import np.com.naxa.staffattendance.data.MyTeamResponse;
import np.com.naxa.staffattendance.database.AttendanceDao;
import np.com.naxa.staffattendance.database.StaffDao;
import np.com.naxa.staffattendance.database.TeamDao;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class MyTeamRepository {

    private StaffDao staffDao;
    private AttendanceDao attendanceDao;


    public MyTeamRepository() {
        staffDao = new StaffDao();
        attendanceDao = new AttendanceDao();
    }

    public Observable<Object> fetchMyTeam() {
        final ApiInterface apiInterface = APIClient.getUploadClient().create(ApiInterface.class);

        return myTeamObservable()
                .filter(new Func1<List<TeamMemberResposne>, Boolean>() {
                    @Override
                    public Boolean call(List<TeamMemberResposne> teamMemberResposnes) {
                        staffDao.removeAllStaffList();
                        String teamId = SharedPreferenceUtils.getFromPrefs(StaffAttendance.getStaffAttendance(), SharedPreferenceUtils.KEY.TeamID, "");
                        return !TextUtils.isEmpty(teamId);
                    }
                })
                .flatMap(new Func1<List<TeamMemberResposne>, Observable<ArrayList<AttendanceResponse>>>() {
                    @Override
                    public Observable<ArrayList<AttendanceResponse>> call(List<TeamMemberResposne> teamMemberResposnes) {
                        String teamId = SharedPreferenceUtils.getFromPrefs(StaffAttendance.getStaffAttendance(), SharedPreferenceUtils.KEY.TeamID, "");
                        staffDao.saveStafflist(teamMemberResposnes);
                        return apiInterface.getPastAttendanceList(teamId);
                    }
                })
                .flatMap(new Func1<ArrayList<AttendanceResponse>, Observable<?>>() {
                    @Override
                    public Observable<?> call(ArrayList<AttendanceResponse> attendanceRespons) {
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
                .flatMapIterable(new Func1<ArrayList<MyTeamResponse>, Iterable<MyTeamResponse>>() {
                    @Override
                    public Iterable<MyTeamResponse> call(ArrayList<MyTeamResponse> myTeamResponses) {
                        return myTeamResponses;
                    }
                })
                .flatMap(new Func1<MyTeamResponse, Observable<TeamMemberResposne>>() {
                    @Override
                    public Observable<TeamMemberResposne> call(final MyTeamResponse myTeamResponse) {
                        final String teamId = myTeamResponse.getId();//get team id
                        SharedPreferenceUtils.saveToPrefs(StaffAttendance.getStaffAttendance().getApplicationContext(), SharedPreferenceUtils.KEY.TeamID, teamId);

                        return apiInterface.getTeamMember(teamId)//request team memeber for each id
                                .flatMapIterable(new Func1<ArrayList<TeamMemberResposne>, Iterable<TeamMemberResposne>>() {
                                    @Override
                                    public Iterable<TeamMemberResposne> call(ArrayList<TeamMemberResposne> teamMemberResposnes) {
                                        return teamMemberResposnes;//make team response iterable (loopable)
                                    }
                                }).flatMap(new Func1<TeamMemberResposne, Observable<TeamMemberResposne>>() {
                                    @Override
                                    public Observable<TeamMemberResposne> call(TeamMemberResposne teamMemberResposne) {
                                        //add team id and team name is team member obj

                                        teamMemberResposne.setTeamID(teamId);
                                        teamMemberResposne.setTeamName(myTeamResponse.getName());
                                        return Observable.just(teamMemberResposne);
                                    }
                                });
                    }
                }).toList();

    }

    private Observable<Object> uploadAttendance(final String teamId, final String date, final ArrayList<TeamMemberResposne> stafflist) {
        final ApiInterface apiInterface = APIClient.getUploadClient().create(ApiInterface.class);

        return staffDao.getStaffIdFromObject(stafflist)
                .flatMap(new Func1<List<String>, Observable<AttendanceResponse>>() {
                    @Override
                    public Observable<AttendanceResponse> call(List<String> stafflist) {
                        return apiInterface.postAttendanceForTeam(teamId, date, stafflist);
                    }
                }).flatMap(new Func1<AttendanceResponse, Observable<?>>() {
                    @Override
                    public Observable<?> call(AttendanceResponse attendanceResponse) {
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
                .flatMapIterable((Func1<ArrayList<AttendanceResponse>, Iterable<AttendanceResponse>>) attendanceRespons -> attendanceRespons)
                .flatMap((Func1<AttendanceResponse, Observable<AttendanceResponse>>) attendanceResponse -> {

                    return apiInterface.postAttendanceForTeam(teamId,
                            attendanceResponse.getAttendanceDate(false),
                            attendanceResponse.getPresentStaffIds());
                })
                .flatMap((Func1<AttendanceResponse, Observable<AttendanceResponse>>) attendanceResponse -> {
                    if (attendanceResponse != null) {
                        attendanceDao.updateAttendance(attendanceResponse.getAttendanceDate(false), teamId);
                    }
                    return null;
                });
    }
}
