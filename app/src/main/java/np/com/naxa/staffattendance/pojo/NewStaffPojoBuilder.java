package np.com.naxa.staffattendance.pojo;

public class NewStaffPojoBuilder {
    private Integer designation;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private Integer gender;
    private String ethnicity;
    private Integer bank;
    private String bankName;
    private String accountNumber;
    private String phoneNumber;
    private String email;
    private String address;
    private String contractStart;
    private String contractEnd;
    private String photo;
    private String status;
    private String id;
    private String idPassDID;

    public NewStaffPojoBuilder setDesignation(Integer designation) {
        this.designation = designation;
        return this;
    }

    public NewStaffPojoBuilder setID(String id) {
        this.id = id;
        return this;
    }


    public NewStaffPojoBuilder setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public NewStaffPojoBuilder setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public NewStaffPojoBuilder setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public NewStaffPojoBuilder setGender(Integer gender) {
        this.gender = gender;
        return this;
    }

    public NewStaffPojoBuilder setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
        return this;
    }

    public NewStaffPojoBuilder setBank(Integer bank) {
        this.bank = bank;
        return this;
    }

    public NewStaffPojoBuilder setBankName(String bankName) {
        this.bankName = bankName;
        return this;
    }

    public NewStaffPojoBuilder setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
        return this;
    }

    public NewStaffPojoBuilder setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public NewStaffPojoBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    public NewStaffPojoBuilder setAddress(String address) {
        this.address = address;
        return this;
    }

    public NewStaffPojoBuilder setContractStart(String contractStart) {
        this.contractStart = contractStart;
        return this;
    }

    public NewStaffPojoBuilder setContractEnd(String contractEnd) {
        this.contractEnd = contractEnd;
        return this;
    }

    public NewStaffPojoBuilder setPhoto(String photo) {
        this.photo = photo;
        return this;
    }

    public NewStaffPojoBuilder setStatus(String status) {
        this.status = status;
        return this;
    }

    public NewStaffPojoBuilder setIDPass(String idPassDID) {
        this.idPassDID = idPassDID;
        return this;
    }

    public NewStaffPojo createNewStaffPojo() {
        return new NewStaffPojo(id, designation, firstName, lastName, dateOfBirth, gender, ethnicity, bank, bankName, accountNumber, phoneNumber, email, address, contractStart, contractEnd, photo, status, idPassDID);
    }
}