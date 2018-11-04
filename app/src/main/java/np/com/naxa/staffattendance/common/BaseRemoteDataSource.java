package np.com.naxa.staffattendance.common;


import io.reactivex.Observable;

public interface BaseRemoteDataSource<T> {

    Observable<Object> getAll();



}
