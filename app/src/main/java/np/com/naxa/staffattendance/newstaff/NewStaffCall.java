package np.com.naxa.staffattendance.newstaff;


import np.com.naxa.staffattendance.data.APIClient;
import np.com.naxa.staffattendance.data.ApiInterface;
import rx.Observable;

public class NewStaffCall {
        public void upload(){
            final ApiInterface apiInterface = APIClient.getUploadClient().create(ApiInterface.class);
//            Observable<NewStaffUploadResponse> observable=apiInterface.uploadNewStaff()
        }
}
