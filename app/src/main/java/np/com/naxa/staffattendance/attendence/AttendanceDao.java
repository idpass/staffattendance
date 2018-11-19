package np.com.naxa.staffattendance.attendence;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface AttendanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Attendance... attendances);

    @Update
    void update(Attendance... attendances);

    @Delete
    void delete(Attendance... attendances);

    @Query("SELECT * FROM attendance")
    LiveData<List<Attendance>> getAll();

    @Query("SELECT * FROM attendance WHERE syncStatus = :status")
    Single<List<Attendance>> getAttendenceByStatus(String status);

    @Query("SELECT * FROM attendance WHERE attendanceDate=:todaysFormattedDate")
    Flowable<Attendance> getAttendanceByDate(String todaysFormattedDate);

    @Query("DELETE FROM attendance")
    void deleteAll();
}
