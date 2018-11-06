package np.com.naxa.staffattendance.pojo;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface StaffDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ArrayList<Staff> staffs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Staff... staffs);

    @Update
    void update(ArrayList<Staff> staff);

    @Delete
    void delete(Staff staff);

    @Query("DELETE from staff")
    void deleteAll();

    @Query("SELECT * from staff")
    LiveData<List<Staff>> getAllStaffs();

    @Query("SELECT * from staff WHERE teamID = :teamId")
    LiveData<List<Staff>> getStaffFromTeamId(String teamId);

    @Query("SELECT * from staff WHERE id = :staffId")
    LiveData<Staff> getStaffFromId(String staffId);

    @Query("SELECT * from staff WHERE status = :status")
    Single<List<Staff>> getStaffFromStatus(String status);

}
