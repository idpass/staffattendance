package np.com.naxa.staffattendance.attendence;

import android.arch.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import np.com.naxa.staffattendance.common.BaseRepository;

public class AttendanceRepository implements BaseRepository<Attendance> {

    private static AttendanceRepository INSTANCE;
    private AttendanceLocalSource localSource;
    private AttendanceRemoteSource remoteSource;

    public AttendanceRepository(AttendanceLocalSource localSource, AttendanceRemoteSource remoteSource) {
        this.localSource = localSource;
        this.remoteSource = remoteSource;
    }

    public static AttendanceRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AttendanceRepository(AttendanceLocalSource.getInstance(), AttendanceRemoteSource.getInstance());
        }
        return INSTANCE;
    }

    @Override
    public LiveData<List<Attendance>> getAll(boolean forceUpdate) {
        return localSource.getAll();
    }

    @Override
    public void save(Attendance... items) {
        localSource.save(items);
    }

    @Override
    public void updateAll(ArrayList<Attendance> items) {

    }

    public void removeAll() {
        localSource.removeAll();
    }

    public ObservableSource<?> save(ArrayList<AttendanceResponse> attendanceResponses) {
        return localSource.save(attendanceResponses);
    }

    public Single<List<Attendance>> getAttendanceByStatus(String status) {
        return localSource.getAttendenceByStatus(status);
    }

    public Flowable<Attendance> getAttendanceByDate(String todaysFormattedDate) {
        return localSource.getAttendenceByDate(todaysFormattedDate);
    }
}
