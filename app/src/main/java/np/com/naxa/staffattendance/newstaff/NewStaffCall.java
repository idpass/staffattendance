package np.com.naxa.staffattendance.newstaff;


import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import java.io.File;

import np.com.naxa.staffattendance.AttendanceFormEditActivity;
import np.com.naxa.staffattendance.application.StaffAttendance;
import np.com.naxa.staffattendance.data.APIClient;
import np.com.naxa.staffattendance.data.ApiInterface;
import np.com.naxa.staffattendance.pojo.NewStaffPojo;
import np.com.naxa.staffattendance.utlils.ToastUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NewStaffCall {

    public void upload(final NewStaffPojo pojo, File photoFileToUpload, final NewStaffCallListener listener) {
        newStaffObservable(pojo, photoFileToUpload).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<NewStaffPojo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ToastUtils.showShort("Error");
                        listener.onError();
                    }

                    @Override
                    public void onNext(NewStaffPojo newStaffPojo) {
                        ToastUtils.showShort("Success");
                        listener.onSuccess();
                    }
                });
    }

    private Observable<NewStaffPojo> newStaffObservable(NewStaffPojo pojo, File photoFileToUpload) {
        Context context = StaffAttendance.getStaffAttendance();
        final ApiInterface apiInterface = APIClient.getUploadClient().create(ApiInterface.class);
        return apiInterface.uploadNewStaff(
                pojo.getDesignation(),
                RequestBody.create(MediaType.parse("text/plain"), pojo.getFirstName()),
                RequestBody.create(MediaType.parse("text/plain"), pojo.getLastName()),
                RequestBody.create(MediaType.parse("text/plain"), pojo.getDateOfBirth()),
                pojo.getGender(),
                RequestBody.create(MediaType.parse("text/plain"), pojo.getEthnicity()),
                pojo.getBank(),
                RequestBody.create(MediaType.parse("text/plain"), pojo.getBankName()),
                RequestBody.create(MediaType.parse("text/plain"), pojo.getAccountNumber()),
                RequestBody.create(MediaType.parse("text/plain"), pojo.getPhoneNumber()),
                RequestBody.create(MediaType.parse("text/plain"), pojo.getEmail()),
                RequestBody.create(MediaType.parse("text/plain"), pojo.getAddress()),
                RequestBody.create(MediaType.parse("text/plain"), pojo.getContractStart()),
                RequestBody.create(MediaType.parse("text/plain"), pojo.getContractEnd()),
                getImageFile(photoFileToUpload)
        );
    }

    private MultipartBody.Part getImageFile(File photo) {
        if (photo == null) {
            return null;
        }

        RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/*"), photo);
        MultipartBody.Part image = MultipartBody.Part.createFormData("photo", photo.getName(), imageRequestBody);

        return image;
    }

    public interface NewStaffCallListener {
        void onError();

        void onSuccess();
    }

}
