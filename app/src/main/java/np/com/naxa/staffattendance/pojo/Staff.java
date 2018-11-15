package np.com.naxa.staffattendance.pojo;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


@Entity(tableName = "staff")
public class Staff {

    @PrimaryKey
    @NonNull
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("first_name")
    @Expose
    private String firstName;

    @SerializedName("last_name")
    @Expose
    private String lastName;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("gender")
    @Expose
    private Integer gender;

    @SerializedName("ethnicity")
    @Expose
    private String ethnicity;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;

    @SerializedName("bank_name")
    @Expose
    private String bankName;

    @SerializedName("account_number")
    @Expose
    private String accountNumber;

    @SerializedName("photo")
    @Expose
    private String photo;

    @SerializedName("designation")
    @Expose
    private Integer designation;

    @SerializedName("date_of_birth")
    @Expose
    private String dateOfBirth;

    @SerializedName("contract_start")
    @Expose
    private String contractStart;

    @SerializedName("contract_end")
    @Expose
    private String contractEnd;

    @SerializedName("bank")
    @Expose
    private Integer bank;

    private String status;

    private String teamID;

    private String teamName;

    private String staffType;

    public Staff(){

    }

    public Staff(@NonNull String id, String firstName, String lastName, String email, Integer gender, String ethnicity, String address, String phoneNumber, String bankName, String accountNumber, String photo, Integer designation, String dateOfBirth, String contractStart, String contractEnd, Integer bank, String status, String teamID, String teamName, String staffType) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.gender = gender;
        this.ethnicity = ethnicity;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.photo = photo;
        this.designation = designation;
        this.dateOfBirth = dateOfBirth;
        this.contractStart = contractStart;
        this.contractEnd = contractEnd;
        this.bank = bank;
        this.status = status;
        this.teamID = teamID;
        this.teamName = teamName;
        this.staffType = staffType;
    }

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
