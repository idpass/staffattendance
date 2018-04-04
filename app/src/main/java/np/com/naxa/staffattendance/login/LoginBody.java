package np.com.naxa.staffattendance.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by samir on 4/2/2018.
 */

public class LoginBody {


    @SerializedName("email_or_username") @Expose
    private static String username;
    @SerializedName("password") @Expose
    private static String password;

    public LoginBody(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
