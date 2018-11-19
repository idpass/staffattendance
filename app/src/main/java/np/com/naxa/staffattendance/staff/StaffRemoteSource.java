package np.com.naxa.staffattendance.staff;

import java.io.File;

import io.reactivex.Observable;
import np.com.naxa.staffattendance.common.BaseRemoteDataSource;
import np.com.naxa.staffattendance.data.APIClient;
import np.com.naxa.staffattendance.data.ApiInterface;
import np.com.naxa.staffattendance.database.TeamDao;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class StaffRemoteSource implements BaseRemoteDataSource<Staff> {

    private static StaffRemoteSource INSTANCE;

    public static StaffRemoteSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StaffRemoteSource();
        }
        return INSTANCE;
    }

    @Override
    public Observable<Object> getAll() {
        return null;
    }

    public Observable<Staff> newStaffObservable(Staff staff, File photoFileToUpload) {
        final ApiInterface apiInterface = APIClient.getUploadClient().create(ApiInterface.class);
        return apiInterface.uploadNewStaff2(
                new TeamDao().getOneTeamIdForDemo(),
                staff.getDesignation(),
                RequestBody.create(MediaType.parse("text/plain"), staff.getFirstName()),
                RequestBody.create(MediaType.parse("text/plain"), staff.getLastName()),
                RequestBody.create(MediaType.parse("text/plain"), staff.getDateOfBirth()),
                staff.getGender(),
                RequestBody.create(MediaType.parse("text/plain"), staff.getEthnicity()),
                staff.getBank(),
                RequestBody.create(MediaType.parse("text/plain"), staff.getBankName()),
                RequestBody.create(MediaType.parse("text/plain"), staff.getAccountNumber()),
                RequestBody.create(MediaType.parse("text/plain"), staff.getPhoneNumber()),
                RequestBody.create(MediaType.parse("tex t/plain"), staff.getEmail()),
                RequestBody.create(MediaType.parse("text/plain"), staff.getAddress()),
                RequestBody.create(MediaType.parse("text/plain"), staff.getContractStart()),
                RequestBody.create(MediaType.parse("text/plain"), staff.getContractEnd()),
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
}
