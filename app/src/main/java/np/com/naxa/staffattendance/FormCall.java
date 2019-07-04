package np.com.naxa.staffattendance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import np.com.naxa.staffattendance.data.APIClient;
import np.com.naxa.staffattendance.data.ApiInterface;
import np.com.naxa.staffattendance.pojo.BankPojo;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class FormCall {

    public Observable<ArrayList<ArrayList<String>>> getDesignation() {
        ApiInterface apiService = APIClient.getUploadClient().create(ApiInterface.class);
        return apiService.getDesignation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
//                .flatMapIterable(new Func1<ArrayList<ArrayList<String>>, Iterable<ArrayList<String>>>() {
//                    @Override
//                    public Iterable<ArrayList<String>> call(ArrayList<ArrayList<String>> arrayLists) {
//                        return arrayLists;
//                    }
//                })
//                .flatMapIterable(new Func1<ArrayList<String>, Iterable<String>>() {
//                    @Override
//                    public Iterable<String> call(ArrayList<String> strings) {
//                        return strings;
//                    }
//                }).filter(new Func1<String, Boolean>() {
//                    @Override
//                    public Boolean call(String s) {
//                        return !TextUtils.isDigitsOnly(s);
//                    }
//                })
//                .toList();
    }

    public Observable<List<List<String>>> getBankList() {
        ApiInterface apiService = APIClient.getUploadClient().create(ApiInterface.class);
        return apiService.getBankist()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapIterable(new Func1<ArrayList<BankPojo>, Iterable<BankPojo>>() {
                    @Override
                    public Iterable<BankPojo> call(ArrayList<BankPojo> bankPojos) {
                        return bankPojos;
                    }
                })
                .map(new Func1<BankPojo, List<String>>() {
                    @Override
                    public List<String> call(BankPojo bankPojo) {
                        return Arrays.asList(bankPojo.getId().toString(), bankPojo.getName());
                    }
                }).toList();

    }


    public interface DesignationListener {
        void designation(ArrayList<ArrayList<String>> arrayLists);
    }

    public interface BankListListener {
        void bankList(ArrayList<BankPojo> arrayLists);
    }
}
