package np.com.naxa.staffattendance.attendence;

import android.arch.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.services.concurrency.AsyncTask;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import np.com.naxa.staffattendance.application.StaffAttendance;
import np.com.naxa.staffattendance.common.BaseLocalDataSource;
import np.com.naxa.staffattendance.common.Constant;
import np.com.naxa.staffattendance.StaffAttendenceDatabase;
import np.com.naxa.staffattendance.utlils.DateConvertor;

public class AttendanceLocalSource implements BaseLocalDataSource<Attendance> {

    private static AttendanceLocalSource INSTANCE;
    private AttendanceDao dao;

    public AttendanceLocalSource() {
        StaffAttendenceDatabase db = StaffAttendenceDatabase.getDatabase(StaffAttendance.getStaffAttendance());
        dao = db.attendenceDao();
    }

    public static AttendanceLocalSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AttendanceLocalSource();
        }
        return INSTANCE;
    }

    @Override
    public LiveData<List<Attendance>> getAll() {
        return dao.getAll();
    }

    @Override
    public void save(Attendance... items) {
        AsyncTask.execute(() -> dao.insert(items));
    }


    @Override
    public void updateAll(ArrayList<Attendance> items) {
    }

    public void removeAll() {
        dao.deleteAll();
    }

    public Single<List<Attendance>> getAttendenceByStatus(String status) {
        return dao.getAttendenceByStatus(status);
    }

    public ObservableSource<?> save(ArrayList<AttendanceResponse> attendanceResponses) {
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                try {
                    String date;

                    for (AttendanceResponse attendanceResponse : attendanceResponses) {
                        if (attendanceResponse.getAttendanceDate(false).equalsIgnoreCase("Today")) {
                            date = DateConvertor.getCurrentDate();
                        } else {
                            date = attendanceResponse.getAttendanceDate(false);
                        }

                        Attendance attendance = new AttendanceBuilder()
                                .setId(attendanceResponse.getId())
                                .setStaffIds(attendanceResponse.getPresentStaffIds().toString())
                                .setAttendanceDate(date)
                                .setSyncStatus(Constant.AttendanceStatus.UPLOADED)
                                .createAttendance();
                        AsyncTask.execute(() -> dao.insert(attendance));
                    }

                    emitter.onNext("ok");
                    emitter.onComplete();
                } catch (Exception e) {
                    e.printStackTrace();
                    emitter.onComplete();
                }
            }
        });
    }

    public Flowable<Attendance> getAttendenceByDate(String todaysFormattedDate) {
        return dao.getAttendanceByDate(todaysFormattedDate);
    }
}
