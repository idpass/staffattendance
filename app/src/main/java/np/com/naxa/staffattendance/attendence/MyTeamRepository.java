package np.com.naxa.staffattendance.attendence;

import java.util.ArrayList;
import java.util.List;

import np.com.naxa.staffattendance.data.APIClient;
import np.com.naxa.staffattendance.data.ApiInterface;
import np.com.naxa.staffattendance.data.MyTeamResponse;
import np.com.naxa.staffattendance.database.AttendanceDao;
import np.com.naxa.staffattendance.database.StaffDao;
import np.com.naxa.staffattendance.database.TeamDao;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
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
                .flatMap(new Func1<List<TeamMemberResposne>, Observable<ArrayList<AttedanceResponse>>>() {
                    @Override
                    public Observable<ArrayList<AttedanceResponse>> call(List<TeamMemberResposne> teamMemberResposnes) {
                        staffDao.removeAllStaffList();
                        staffDao.saveStafflist(teamMemberResposnes);
                        String teamId = teamMemberResposnes.get(0).getTeamID();
                        return apiInterface.getPastAttendanceList(teamId);
                    }
                })
                .flatMap(new Func1<ArrayList<AttedanceResponse>, Observable<?>>() {
                    @Override
                    public Observable<?> call(ArrayList<AttedanceResponse> attedanceResponses) {
                        attendanceDao.removeAllAttedance();
                        return attendanceDao.saveAttendance(attedanceResponses);
                    }
                });


    }

    private Action1<Throwable> defaultErrorHanlder() {
        return new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();

            }
        };
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
                        String teamId = myTeamResponse.getId();//get team id
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
                                        teamMemberResposne.setTeamID(myTeamResponse.getId());
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
                .flatMap(new Func1<List<String>, Observable<AttedanceResponse>>() {
                    @Override
                    public Observable<AttedanceResponse> call(List<String> stafflist) {
                        return apiInterface.postAttendanceForTeam(teamId, date, stafflist);
                    }
                }).flatMap(new Func1<AttedanceResponse, Observable<?>>() {
                    @Override
                    public Observable<?> call(AttedanceResponse attedanceResponse) {
                        if (attedanceResponse != null) {
                            attendanceDao.updateAttendance(attedanceResponse.getAttendanceDate(false), teamId);
                        }
                        return null;
                    }
                });
    }


    public Observable<Object> bulkAttendanceUpload() {
        final ApiInterface apiInterface = APIClient.getUploadClient().create(ApiInterface.class);
        final String teamId = new TeamDao().getOneTeamIdForDemo();

        return Observable.just(attendanceDao.getFinalizedAttendanceSheet())
                .flatMapIterable(new Func1<ArrayList<AttedanceResponse>, Iterable<AttedanceResponse>>() {
                    @Override
                    public Iterable<AttedanceResponse> call(ArrayList<AttedanceResponse> attedanceResponses) {
                        return attedanceResponses;
                    }
                })
                .flatMap(new Func1<AttedanceResponse, Observable<AttedanceResponse>>() {
                    @Override
                    public Observable<AttedanceResponse> call(AttedanceResponse attedanceResponse) {

                        return apiInterface.postAttendanceForTeam(teamId, attedanceResponse.getAttendanceDate(false), attedanceResponse.getStaffs());
                    }
                })
                .flatMap(new Func1<AttedanceResponse, Observable<?>>() {
                    @Override
                    public Observable<?> call(AttedanceResponse attedanceResponse) {
                        if (attedanceResponse != null) {
                            attendanceDao.updateAttendance(attedanceResponse.getAttendanceDate(false), teamId);
                        }
                        return null;

                    }
                });


    }
}
