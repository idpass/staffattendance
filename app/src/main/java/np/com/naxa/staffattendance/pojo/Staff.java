package np.com.naxa.staffattendance.pojo;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "users")
public class Staff {

    @SerializedName("id")
    @Expose
    @PrimaryKey
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "first_name")
    @SerializedName("first_name")
    @Expose
    private String firstName;

    @ColumnInfo(name = "last_name")
    @SerializedName("last_name")
    @Expose
    private String lastName;

    @ColumnInfo(name = "email")
    @SerializedName("email")
    @Expose
    private String email;

    @ColumnInfo(name = "gender")
    @SerializedName("gender")
    @Expose
    private Integer gender;

    @ColumnInfo(name = "ethnicity")
    @SerializedName("ethnicity")
    @Expose
    private String ethnicity;

    @ColumnInfo(name = "address")
    @SerializedName("address")
    @Expose
    private String address;

    @ColumnInfo(name = "phone_number")
    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;

    @ColumnInfo(name = "bank_name")
    @SerializedName("bank_name")
    @Expose
    private String bankName;

    @ColumnInfo(name = "account_number")
    @SerializedName("account_number")
    @Expose
    private String accountNumber;

    @ColumnInfo(name = "photo")
    @SerializedName("photo")
    @Expose
    private String photo;

    @ColumnInfo(name = "designation")
    @SerializedName("designation")
    @Expose
    private Integer designation;

    @ColumnInfo(name = "dob")
    @SerializedName("date_of_birth")
    @Expose
    private String dateOfBirth;

    @ColumnInfo(name = "contract_start_date")
    @SerializedName("contract_start")
    @Expose
    private String contractStart;

    @ColumnInfo(name = "contract_end_date")
    @SerializedName("contract_end")
    @Expose
    private String contractEnd;

    @ColumnInfo(name = "bank_id")
    @SerializedName("bank")
    @Expose
    private Integer bank;

    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "team_id")
    private String teamID;

    @ColumnInfo(name = "team_name")
    private String teamName;

    @ColumnInfo(name = "staff_type")
    private String staffType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Integer getDesignation() {
        return designation;
    }

    public void setDesignation(Integer designation) {
        this.designation = designation;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getContractStart() {
        return contractStart;
    }

    public void setContractStart(String contractStart) {
        this.contractStart = contractStart;
    }

    public String getContractEnd() {
        return contractEnd;
    }

    public void setContractEnd(String contractEnd) {
        this.contractEnd = contractEnd;
    }

    public Integer getBank() {
        return bank;
    }

    public void setBank(Integer bank) {
        this.bank = bank;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTeamID() {
        return teamID;
    }

    public void setTeamID(String teamID) {
        this.teamID = teamID;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getStaffType() {
        return staffType;
    }

    public void setStaffType(String staffType) {
        this.staffType = staffType;
    }
}
