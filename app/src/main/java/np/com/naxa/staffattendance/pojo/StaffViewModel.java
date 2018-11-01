package np.com.naxa.staffattendance.pojo;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class StaffViewModel extends AndroidViewModel {

    private StaffRepository staffRepository;
    private LiveData<List<Staff>> staff;

    public StaffViewModel(@NonNull Application application) {
        super(application);
        staffRepository = new StaffRepository(application);
        staff = staffRepository.getmStaffs();
    }
}
