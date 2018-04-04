package np.com.naxa.staffattendance.data;

import np.com.naxa.staffattendance.login.LoginBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by samir on 4/2/2018.
 */

public interface ApiInterface {

    @FormUrlEncoded
    @POST("users/api/get-auth-token/")
//    Call<LoginResponse> getLoginDetails(@Body LoginBody loginBody);
    Call<LoginResponse> getLoginDetails(@Field("email_or_username") String username,
                                        @Field("password") String password);
}
