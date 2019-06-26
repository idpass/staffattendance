package np.com.naxa.staffattendance.attendence;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TeamMemberResposne {

    private String teamID;
    private String teamName;

    @SerializedName("IdPassDID")
    private String IDPassDID;

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
    private Object email;
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
    private Object dateOfBirth;
    @SerializedName("contract_start")
    @Expose
    private Object contractStart;
    @SerializedName("contract_end")
    @Expose
    private Object contractEnd;
    @SerializedName("bank")
    @Expose
    private Object bank;

    public String getId() {
        return id;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamID() {
        return teamID;

    }

    public TeamMemberResposne(String teamID, String teamName, String id, String firstName, String lastName, Object email, Integer gender, String ethnicity, String address, String phoneNumber, String bankName, String accountNumber, String photo, Integer designation, Object dateOfBirth, Object contractStart, Object contractEnd, Object bank, String IDPassDID) {
        this.teamID = teamID;
        this.teamName = teamName;
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
        this.IDPassDID = IDPassDID;
    }


    public String getIDPassDID() {
        return IDPassDID;
    }

    public void setIDPassDID(String IDPassDID) {
        this.IDPassDID = IDPassDID;
    }

    public void setTeamID(String teamID) {
        this.teamID = teamID;
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

    public Object getEmail() {
        return email;
    }

    public void setEmail(Object email) {
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

    public Object getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Object dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Object getContractStart() {
        return contractStart;
    }

    public void setContractStart(Object contractStart) {
        this.contractStart = contractStart;
    }

    public Object getContractEnd() {
        return contractEnd;
    }

    public void setContractEnd(Object contractEnd) {
        this.contractEnd = contractEnd;
    }

    public Object getBank() {
        return bank;
    }

    public void setBank(Object bank) {
        this.bank = bank;
    }

}
