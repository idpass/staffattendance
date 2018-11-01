package np.com.naxa.staffattendance.pojo;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class StaffRepository {

    private StaffDao mStaffDao;
    private LiveData<List<Staff>> mStaffs;

    public StaffRepository(Application application) {
        StaffAttendenceDatabase db = StaffAttendenceDatabase.getDatabase(application);
        mStaffDao = db.staffDao();
        mStaffs = mStaffDao.getAllStaffs();
    }

    public LiveData<List<Staff>> getmStaffs() {
        return mStaffs;
    }
}
