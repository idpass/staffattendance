package np.com.naxa.staffattendance;

import android.content.Context;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import np.com.naxa.staffattendance.application.StaffAttendance;
import np.com.naxa.staffattendance.data.APIClient;
import np.com.naxa.staffattendance.data.ApiInterface;
import np.com.naxa.staffattendance.login.LoginActivity;
import np.com.naxa.staffattendance.pojo.BankPojo;



public class FormCall {

    public Observable<ArrayList<ArrayList<String>>> getDesignation() {
        ApiInterface apiService = APIClient.getUploadClient().create(ApiInterface.class);
        return apiService.getDesignation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .map(new Function<ArrayList<ArrayList<String>>, ArrayList<ArrayList<String>>>() {
                    @Override
                    public ArrayList<ArrayList<String>> apply(ArrayList<ArrayList<String>> designationList) {
                        Context context = StaffAttendance.getStaffAttendance().getApplicationContext();

                        SharedPreferenceUtils
                                .saveToPrefs(context, SharedPreferenceUtils.KEY.Designation_v2,
                                        new Gson().toJson(designationList));
                        return designationList;
                    }
                });

    }


    public Observable<List<String>> getBankList(  ) {
        ApiInterface apiService = APIClient.getUploadClient().create(ApiInterface.class);
        return apiService.getBankist()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<ArrayList<BankPojo>, ArrayList<BankPojo>>() {
                    @Override
                    public ArrayList<BankPojo> apply(ArrayList<BankPojo> bankPojos) {
                        Context context = StaffAttendance.getStaffAttendance().getApplicationContext();
                        SharedPreferenceUtils
                                .saveToPrefs(context, SharedPreferenceUtils.KEY.Bank_v2,
                                        new Gson().toJson(bankPojos));
                        return bankPojos;
                    }
                })
                .flatMapIterable(new Function<ArrayList<BankPojo>, Iterable<BankPojo>>() {
                    @Override
                    public Iterable<BankPojo> apply(ArrayList<BankPojo> bankPojos) {
                        return bankPojos;
                    }
                })
                .flatMap(new Function<BankPojo, Observable<String>>() {
                    @Override
                    public Observable<String> apply(BankPojo bankPojo) {
                        return Observable.just(bankPojo.getName());
                    }
                }).toList().toObservable();

    }


    public interface DesignationListener {
        void designation(ArrayList<ArrayList<String>> arrayLists);
    }

    public interface BankListListener {
        void bankList(ArrayList<BankPojo> arrayLists);
    }
}
