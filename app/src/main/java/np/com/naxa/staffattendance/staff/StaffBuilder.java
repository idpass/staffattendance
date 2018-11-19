package np.com.naxa.staffattendance.staff;

public class StaffBuilder {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private Integer gender;
    private String ethnicity;
    private String address;
    private String phoneNumber;
    private String bankName;
    private String accountNumber;
    private String photo;
    private Integer designation;
    private String dateOfBirth;
    private String contractStart;
    private String contractEnd;
    private Integer bank;
    private String status;
    private String teamID;
    private String teamName;
    private String staffType;

    public StaffBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public StaffBuilder setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public StaffBuilder setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public StaffBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    public StaffBuilder setGender(Integer gender) {
        this.gender = gender;
        return this;
    }

    public StaffBuilder setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
        return this;
    }

    public StaffBuilder setAddress(String address) {
        this.address = address;
        return this;
    }

    public StaffBuilder setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public StaffBuilder setBankName(String bankName) {
        this.bankName = bankName;
        return this;
    }

    public StaffBuilder setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
        return this;
    }

    public StaffBuilder setPhoto(String photo) {
        this.photo = photo;
        return this;
    }

    public StaffBuilder setDesignation(Integer designation) {
        this.designation = designation;
        return this;
    }

    public StaffBuilder setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public StaffBuilder setContractStart(String contractStart) {
        this.contractStart = contractStart;
        return this;
    }

    public StaffBuilder setContractEnd(String contractEnd) {
        this.contractEnd = contractEnd;
        return this;
    }

    public StaffBuilder setBank(Integer bank) {
        this.bank = bank;
        return this;
    }

    public StaffBuilder setStatus(String status) {
        this.status = status;
        return this;
    }

    public StaffBuilder setTeamID(String teamID) {
        this.teamID = teamID;
        return this;
    }

    public StaffBuilder setTeamName(String teamName) {
        this.teamName = teamName;
        return this;
    }

    public StaffBuilder setStaffType(String staffType) {
        this.staffType = staffType;
        return this;
    }

    public Staff createStaff() {
        return new Staff(id, firstName, lastName, email, gender, ethnicity, address, phoneNumber, bankName, accountNumber, photo, designation, dateOfBirth, contractStart, contractEnd, bank, status, teamID, teamName, staffType);
    }
}