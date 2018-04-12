package np.com.naxa.staffattendance.data;


import java.util.ArrayList;

import np.com.naxa.staffattendance.login.LoginResponse;
import np.com.naxa.staffattendance.pojo.NewStaffPojo;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
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
    @POST("staff/api/staff/1")
    Observable<NewStaffPojo> uploadNewStaff(
            @Part("designation") RequestBody designation,
            @Part("first_name") RequestBody firstName,
            @Part("last_name") RequestBody lastName,
            @Part("date_of_birth") RequestBody dob,
            @Part("gender") RequestBody gender,
            @Part("ethnicity") RequestBody ethnicity,
            @Part("bank") RequestBody bankName,
            @Part("account_number") RequestBody accountNumber,
            @Part("phone_number") RequestBody phoneNumber,
            @Part("email") RequestBody email,
            @Part("contract_start") RequestBody contractStartDate,
            @Part("contract_end") RequestBody contractEndDate,
            @Part("bank_name") RequestBody bankNameOther,
            @Part MultipartBody.Part photo
    );

    @GET("/staff/api/designations/")
    Observable<ArrayList<ArrayList<String>>> getDesignation();

    @GET("staff/api/banks/")
    Observable<ArrayList<ArrayList<String>>> getBankist();



}
