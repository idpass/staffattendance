package np.com.naxa.staffattendance;

import android.content.Context;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import np.com.naxa.staffattendance.application.StaffAttendance;
import np.com.naxa.staffattendance.data.APIClient;
import np.com.naxa.staffattendance.data.ApiInterface;
import np.com.naxa.staffattendance.login.LoginActivity;
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
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<ArrayList<ArrayList<String>>, ArrayList<ArrayList<String>>>() {
                    @Override
                    public ArrayList<ArrayList<String>> call(ArrayList<ArrayList<String>> designationList) {
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
                .map(new Func1<ArrayList<BankPojo>, ArrayList<BankPojo>>() {
                    @Override
                    public ArrayList<BankPojo> call(ArrayList<BankPojo> bankPojos) {
                        Context context = StaffAttendance.getStaffAttendance().getApplicationContext();
                        SharedPreferenceUtils
                                .saveToPrefs(context, SharedPreferenceUtils.KEY.Bank_v2,
                                        new Gson().toJson(bankPojos));
                        return bankPojos;
                    }
                })
                .flatMapIterable(new Func1<ArrayList<BankPojo>, Iterable<BankPojo>>() {
                    @Override
                    public Iterable<BankPojo> call(ArrayList<BankPojo> bankPojos) {
                        return bankPojos;
                    }
                })
                .flatMap(new Func1<BankPojo, Observable<String>>() {
                    @Override
                    public Observable<String> call(BankPojo bankPojo) {
                        return Observable.just(bankPojo.getName());
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
