package np.com.naxa.staffattendance.login;

import com.google.gson.annotations.SerializedName;

/**
 * Created by samir on 4/2/2018.
 */

public class LoginResponse {
    @SerializedName("token")
    String token;
    @SerializedName("non_field_errors")
    String non_field_errors;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNon_field_errors() {
        return non_field_errors;
    }

    public void setNon_field_errors(String non_field_errors) {
        this.non_field_errors = non_field_errors;
    }
}
