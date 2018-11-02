package np.com.naxa.staffattendance.common;

import rx.Observable;

public interface BaseRemoteDataSource<T> {

    Observable<Object> getAll();



}
