package np.com.naxa.staffattendance.pojo;

import android.arch.lifecycle.LiveData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import np.com.naxa.staffattendance.common.BaseRepository;


public class StaffRepository implements BaseRepository<Staff> {

    private static StaffRepository INSTANCE;
    private StaffLocalSource localSource;
    private StaffRemoteSource remoteSource;

    public StaffRepository(StaffLocalSource localSource, StaffRemoteSource remoteSource) {
        this.localSource = localSource;
        this.remoteSource = remoteSource;
    }

    public static StaffRepository getInstance() {
        return getInstance(StaffLocalSource.getInstance(), StaffRemoteSource.getInstance());
    }

    public static StaffRepository getInstance(StaffLocalSource localSource, StaffRemoteSource remoteSource) {
        if (INSTANCE == null) {
            INSTANCE = new StaffRepository(localSource, remoteSource);
        }
        return INSTANCE;
    }

    @Override
    public LiveData<List<Staff>> getAll(boolean forceUpdate) {
        return localSource.getAll();
    }

    @Override
    public void save(Staff... items) {
        localSource.save(items);
    }

    public void save(ArrayList<Staff> items) {
        localSource.save(items);
    }

    @Override
    public void updateAll(ArrayList<Staff> items) {
        localSource.updateAll(items);
    }

    public Single<List<Staff>> getStaffFromStatus(String status) {
        return localSource.getStaffFromStatus(status);
    }

    public Observable<Staff> upload(Staff staff, File filePhoto) {
        return remoteSource.newStaffObservable(staff, filePhoto);
    }

    public LiveData<List<Staff>> getStaffByTeamId(String teamId) {
        return localSource.getStaffFromTeamId(teamId);
    }

    public void deleteAll() {
        localSource.deleteAll();
    }

    public void deleteStaff(Staff staff) {
        localSource.deleteStaff(staff);
    }


    public LiveData<Staff> getStaffFromId(String staffId) {
        return localSource.getStaffFromId(staffId);
    }
}
