package np.com.naxa.staffattendance.newstaff;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import np.com.naxa.staffattendance.SharedPreferenceUtils;
import np.com.naxa.staffattendance.application.StaffAttendance;
import np.com.naxa.staffattendance.common.BaseLocalDataSource;
import np.com.naxa.staffattendance.pojo.BankPojo;

public class BankLocalSource implements BaseLocalDataSource<BankPojo> {

    private static BankLocalSource INSTANCE;
    private Gson gson;
    private Type typeToken = null;

    private BankLocalSource() {
        this.gson = new Gson();
        this.typeToken = new TypeToken<ArrayList<String>>() {
        }.getType();

    }

    public static BankLocalSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BankLocalSource();
        }
        return INSTANCE;
    }


    @Override
    public LiveData<List<BankPojo>> getAll() {
        String banks = SharedPreferenceUtils.getFromPrefs(StaffAttendance.getStaffAttendance(), SharedPreferenceUtils.KEY.Bank, "");
        List<BankPojo> bankPojoList = gson.fromJson(banks, typeToken);
        MutableLiveData<List<BankPojo>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(bankPojoList);
        return mutableLiveData;
    }

    public Observable<List<Pair<Integer, String>>> getAsPairs() {

        return Observable.create(e -> {
            try {
                List<Pair<Integer, String>> pairs = new ArrayList<>();

                String banks = SharedPreferenceUtils.getFromPrefs(StaffAttendance.getStaffAttendance(), SharedPreferenceUtils.KEY.Bank, "");
                if (banks == null || banks.length() == 0)
                    throw new IllegalArgumentException("Banks is not present in shared pref");

                List<BankPojo> bankPojoList = gson.fromJson(banks, typeToken);
                for (BankPojo bankPojo : bankPojoList) {
                    Pair<Integer, String> pair = Pair.create(bankPojo.getId(), bankPojo.getName());
                    pairs.add(pair);
                }

                e.onNext(pairs);
                e.onComplete();
            } catch (Exception ex) {
                e.onError(ex);
            }
        });


    }

    @Override
    public void save(BankPojo... items) {

    }

    @Override
    public void save(ArrayList<BankPojo> items) {

    }

    @Override
    public void updateAll(ArrayList<BankPojo> items) {

    }
}
