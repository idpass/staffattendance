package np.com.naxa.staffattendance;

import rx.Observable;

public interface BaseRemoteDataSource<T> {

    Observable<Object> getAll();



}
