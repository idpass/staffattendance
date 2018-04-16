package np.com.naxa.staffattendance.attendence;

import java.util.ArrayList;
import java.util.List;

import np.com.naxa.staffattendance.data.APIClient;
import np.com.naxa.staffattendance.data.ApiInterface;
import np.com.naxa.staffattendance.data.MyTeamResponse;
import np.com.naxa.staffattendance.database.AttendanceDao;
import np.com.naxa.staffattendance.database.StaffDao;
import np.com.naxa.staffattendance.database.TeamDao;
import np.com.naxa.staffattendance.utlils.ToastUtils;
import rx.Observable;
import rx.Observer;
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

    public void fetchMyTeam() {
        myTeamObservable()
                .subscribe(new Observer<List<TeamMemberResposne>>() {
                    @Override
                    public void onCompleted() {
                        //todo  do not do this, make this request tied with rx in future
                        String teamId = new TeamDao().getOneTeamIdForDemo();
                        fetchPastAttendance(teamId);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<TeamMemberResposne> teamMemberResposnes) {
                        staffDao.saveStafflist(teamMemberResposnes);
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

    public void uploadAttendance(final String teamId, final String date, final ArrayList<TeamMemberResposne> stafflist) {
        final ApiInterface apiInterface = APIClient.getUploadClient().create(ApiInterface.class);

        staffDao.getStaffIdFromObject(stafflist)
                .flatMap(new Func1<List<String>, Observable<AttedanceResponse>>() {
                    @Override
                    public Observable<AttedanceResponse> call(List<String> stafflist) {
                        return apiInterface.postAttendanceForTeam(teamId, date, stafflist);
                    }
                })
                .subscribe(new Observer<AttedanceResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showLong(String.format("Failed to upload reason %s", e.getMessage()));
                    }

                    @Override
                    public void onNext(AttedanceResponse staff) {
                        if (staff != null) {
                            ArrayList<AttedanceResponse> list = new ArrayList<>();
                            list.add(staff);
                            attendanceDao.saveAttendance(list);
                        }
                    }
                });

    }

    public void fetchPastAttendance(String teamID) {
        final ApiInterface apiInterface = APIClient
                .getUploadClient()
                .create(ApiInterface.class);

        apiInterface.getPastAttendanceList(teamID)
                .subscribe(new Observer<ArrayList<AttedanceResponse>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(ArrayList<AttedanceResponse> attedanceResponses) {
                        attendanceDao.saveAttendance(attedanceResponses);
                    }
                });

    }

}
