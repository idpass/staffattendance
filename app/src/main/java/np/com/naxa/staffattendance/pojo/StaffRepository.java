package np.com.naxa.staffattendance.pojo;

import android.arch.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import np.com.naxa.staffattendance.common.BaseRepository;

public class StaffRepository implements BaseRepository<Staff> {

    private static StaffRepository INSTANCE;
    private static StaffLocalSource localSource;
    private static StaffRemoteSource remoteSource;

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

    @Override
    public void save(ArrayList<Staff> items) {
        localSource.save(items);
    }

    @Override
    public void updateAll(ArrayList<Staff> items) {
        localSource.updateAll(items);
    }
}
