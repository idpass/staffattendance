package np.com.naxa.staffattendance.newstaff;


import android.content.Context;

import java.io.File;

import np.com.naxa.staffattendance.application.StaffAttendance;
import np.com.naxa.staffattendance.data.APIClient;
import np.com.naxa.staffattendance.data.ApiInterface;
import np.com.naxa.staffattendance.database.NewStaffDao;
import np.com.naxa.staffattendance.database.TeamDao;
import np.com.naxa.staffattendance.pojo.NewStaffPojo;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
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
                        listener.onError();
                    }

                    @Override
                    public void onNext(NewStaffPojo newStaffPojo) {
                        new NewStaffDao().deleteStaffById(String.valueOf(newStaffPojo.getId()));
                        listener.onSuccess();
                    }
                });
    }


    public Observable<NewStaffPojo> newStaffObservable(NewStaffPojo pojo, File photoFileToUpload) {
        Context context = StaffAttendance.getStaffAttendance();
        final ApiInterface apiInterface = APIClient.getUploadClient().create(ApiInterface.class);
        return apiInterface.uploadNewStaff(
                new TeamDao().getOneTeamIdForDemo(),
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
                RequestBody.create(MediaType.parse("text/plain"), pojo.getIdPassDID()),
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
