package np.com.naxa.staffattendance;

import java.util.ArrayList;

import np.com.naxa.staffattendance.data.APIClient;
import np.com.naxa.staffattendance.data.ApiInterface;
import np.com.naxa.staffattendance.utlils.ToastUtils;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FormCall {

    public void getDesignation(final DesignationListener listener) {
        ApiInterface apiService = APIClient.getUploadClient().create(ApiInterface.class);
        apiService.getDesignation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<ArrayList<String>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ArrayList<ArrayList<String>> arrayLists) {
                        listener.designation(arrayLists);
                    }
                });

    }

    public void getBankList(final BankListListener listener) {
        ApiInterface apiService = APIClient.getUploadClient().create(ApiInterface.class);
        apiService.getBankist()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<ArrayList<String>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ArrayList<ArrayList<String>> arrayLists) {
                                              listener.bankList(arrayLists);
                    }
                });
    }


    public interface DesignationListener {
        void designation(ArrayList<ArrayList<String>> arrayLists);
    }

    public interface BankListListener {
        void bankList(ArrayList<ArrayList<String>> arrayLists);
    }
}
