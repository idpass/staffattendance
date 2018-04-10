package np.com.naxa.staffattendance.data;


import np.com.naxa.staffattendance.login.LoginBody;
import np.com.naxa.staffattendance.pojo.NewStaffPojo;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by samir on 4/2/2018.
 */

public interface ApiInterface {

    @FormUrlEncoded
    @POST("users/api/get-auth-token/")
    Call<LoginResponse> getLoginDetails(
            @Field("email_or_username") String username,
            @Field("password") String password
    );

    @Multipart
    @POST("staff/api/staff/")
    Observable<NewStaffPojo> uploadNewStaff(
            @Part("designation") RequestBody back_window,
            @Part("first_name") RequestBody roof_type,
            @Part("last_name") RequestBody room_length,
            @Part("gender") RequestBody room_width,
            @Part("ethnicity") RequestBody front_door,
            @Part("address") RequestBody back_door,
            @Part("phone_number") RequestBody left_floor,
            @Part("bank_name") RequestBody right_door,
            @Part("account_number") RequestBody front_window,
            @Part("contract_start") RequestBody front_window,
            @Part("contract_end") RequestBody front_window,
            @Part("bank") RequestBody front_window,
            @Part MultipartBody.Part photo
    );
}
