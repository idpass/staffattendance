package np.com.naxa.staffattendance.attendence;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Arrays;
import java.util.Date;

import io.reactivex.subscribers.DisposableSubscriber;
import np.com.naxa.staffattendance.DailyAttendanceFragment;
import np.com.naxa.staffattendance.database.AttendanceDao;
import np.com.naxa.staffattendance.database.TeamDao;
import np.com.naxa.staffattendance.utlils.DateConvertor;


public class AttendanceViewPagerAdapter extends FragmentPagerAdapter {

    public static final int TOTAL_NO_OF_DAYS = 7;
    private AttendanceDao attendanceDao;
    private TeamDao teamDao;
    private String teamId;
    private final String TAG = this.getClass().getSimpleName();


    AttendanceViewPagerAdapter(FragmentManager fm) {
        super(fm);
        attendanceDao = new AttendanceDao();
        teamDao = new TeamDao();
        teamId = teamDao.getOneTeamIdForDemo();
    }

    @Override
    public Fragment getItem(int position) {
        DailyAttendanceFragment fragment = new DailyAttendanceFragment();
        String todaysFormattedDate = DateConvertor.formatDate(getDateForPosition(position));

        AttendanceRepository attendanceRepository = AttendanceRepository.getInstance();

        Attendance[] currentAttendence = new Attendance[1];
        currentAttendence[0] = null;
        attendanceRepository.getAttendanceByDate(todaysFormattedDate)
                .subscribe(new DisposableSubscriber<Attendance>() {
                    @Override
                    public void onNext(Attendance attendance) {
                        currentAttendence[0] = attendance;
                        String staffIDs = attendance.getStaffIds();
                        String[] staffIDlist = staffIDs.replace("[", "").replace("]", "").split(",");
                        fragment.setAttendanceIds(Arrays.asList(staffIDlist), todaysFormattedDate);
                    }

                    @Override
                    public void onError(Throwable t) {
                        t.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        if (currentAttendence[0] == null) {
            fragment.setAttendanceIds(null, todaysFormattedDate);
        }

//        AttendanceResponse dailyAttendance = attendanceDao.getAttedanceByDate(teamId, todaysFormattedDate);
//        fragment.setAttendanceIds(dailyAttendance.getPresentStaffIds(), todaysFormattedDate);

        return fragment;
    }

    @Override
    public int getCount() {
        return TOTAL_NO_OF_DAYS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Date date = getDateForPosition(position);
        String formattedDate;

        boolean isToday = DateConvertor.formatDate(date).equals(DateConvertor.formatDate(new Date()));

        if (isToday) {
            formattedDate = "Today";
        } else {
            formattedDate = DateConvertor.formatDate(date);
        }

        return formattedDate;
    }

    private Date getDateForPosition(int pos) {
        Date date;

        switch (pos) {
            case 0:
                date = DateConvertor.getPastDate(-6);
                break;
            case 1:
                date = DateConvertor.getPastDate(-5);
                break;
            case 2:
                date = DateConvertor.getPastDate(-4);
                break;
            case 3:
                date = DateConvertor.getPastDate(-3);
                break;
            case 4:
                date = DateConvertor.getPastDate(-2);
                break;
            case 5:
                date = DateConvertor.getPastDate(-1);
                break;
            default:
                date = new Date();
                break;
        }


        return date;
    }
}
