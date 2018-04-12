package np.com.naxa.staffattendance.pojo;

public class NewStaffPojo {

    private String designation,
            firstName,
            lastName,
            dob,
            gender,
            ethnicity,
            bankName,
            accountNumber,
            contactNumber,
            email,
            address,
            contractStartDate,
            contractEndDate,
            photoLocation,
            bankId,
            status;

    public NewStaffPojo() {
    }

    public NewStaffPojo(String designation, String firstName, String lastName, String dob, String gender, String ethnicity, String bankName, String accountNumber, String contactNumber, String email, String address, String contractStartDate, String contractEndDate, String photoLocation, String bankId, String status) {
        this.designation = designation;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.gender = gender;
        this.ethnicity = ethnicity;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.contactNumber = contactNumber;
        this.email = email;
        this.address = address;
        this.contractStartDate = contractStartDate;
        this.contractEndDate = contractEndDate;
        this.photoLocation = photoLocation;
        this.bankId = bankId;
        this.status = status;
    }


    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDesignation() {
        return designation;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
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

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContractStartDate() {
        return contractStartDate;
    }

    public void setContractStartDate(String contractStartDate) {
        this.contractStartDate = contractStartDate;
    }

    public String getContractEndDate() {
        return contractEndDate;
    }

    public void setContractEndDate(String contractEndDate) {
        this.contractEndDate = contractEndDate;
    }

    public String getPhotoLocation() {
        return photoLocation;
    }

    public void setPhotoLocation(String photoLocation) {
        this.photoLocation = photoLocation;
    }
}
