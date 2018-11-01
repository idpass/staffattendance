package np.com.naxa.staffattendance.pojo;

import android.arch.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.services.concurrency.AsyncTask;
import np.com.naxa.staffattendance.application.StaffAttendance;
import np.com.naxa.staffattendance.common.BaseLocalDataSource;

public class StaffLocalSource implements BaseLocalDataSource<Staff> {

    private static StaffLocalSource INSTANCE;
    private StaffDao dao;

    public static StaffLocalSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StaffLocalSource();
        }
        return INSTANCE;
    }

    public StaffLocalSource() {
        StaffAttendenceDatabase db = StaffAttendenceDatabase.getDatabase(StaffAttendance.getStaffAttendance());
        dao = db.staffDao();
    }

    @Override
    public LiveData<List<Staff>> getAll() {
        return dao.getAllStaffs();
    }

    @Override
    public void save(Staff... items) {
        AsyncTask.execute(() -> dao.insert(items));
    }

    @Override
    public void save(ArrayList<Staff> items) {
        AsyncTask.execute(() -> dao.insert(items));
    }

    @Override
    public void updateAll(ArrayList<Staff> items) {
        AsyncTask.execute(() -> dao.update(items));
    }

    public LiveData<Staff> getStaffById(String staffId) {
        return dao.getStaffFromId(staffId);
    }

    public LiveData<List<Staff>> getStaffByTeamId(String teamId) {
        return dao.getStaffFromTeamId(teamId);
    }


}
