package np.com.naxa.staffattendance.attendence;

public class TeamMemberResposneBuilder {
    private String teamID;
    private String teamName;
    private String id;
    private String firstName;
    private String lastName;
    private Object email;
    private Integer gender;
    private String ethnicity;
    private String address;
    private String phoneNumber;
    private String bankName;
    private String accountNumber;
    private String photo;
    private Integer designation;
    private Object dateOfBirth;
    private Object contractStart;
    private Object contractEnd;
    private Object bank;
    private String IDPassDID;
    private String designationLabel;

    public TeamMemberResposneBuilder setTeamID(String teamID) {
        this.teamID = teamID;
        return this;
    }

    public TeamMemberResposneBuilder setTeamName(String teamName) {
        this.teamName = teamName;
        return this;
    }

    public TeamMemberResposneBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public TeamMemberResposneBuilder setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public TeamMemberResposneBuilder setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public TeamMemberResposneBuilder setEmail(Object email) {
        this.email = email;
        return this;
    }

    public TeamMemberResposneBuilder setGender(Integer gender) {
        this.gender = gender;
        return this;
    }

    public TeamMemberResposneBuilder setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
        return this;
    }

    public TeamMemberResposneBuilder setAddress(String address) {
        this.address = address;
        return this;
    }

    public TeamMemberResposneBuilder setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public TeamMemberResposneBuilder setBankName(String bankName) {
        this.bankName = bankName;
        return this;
    }

    public TeamMemberResposneBuilder setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
        return this;
    }

    public TeamMemberResposneBuilder setPhoto(String photo) {
        this.photo = photo;
        return this;
    }

    public TeamMemberResposneBuilder setDesignation(Integer designation) {
        this.designation = designation;
        return this;
    }

    public TeamMemberResposneBuilder setDateOfBirth(Object dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public TeamMemberResposneBuilder setContractStart(Object contractStart) {
        this.contractStart = contractStart;
        return this;
    }

    public TeamMemberResposneBuilder setContractEnd(Object contractEnd) {
        this.contractEnd = contractEnd;
        return this;
    }

    public TeamMemberResposneBuilder setBank(Object bank) {
        this.bank = bank;
        return this;
    }

    public TeamMemberResposneBuilder setIDPassDID(String IDPassDID) {
        this.IDPassDID = IDPassDID;
        return this;
    }

    public TeamMemberResposneBuilder setDesignationLabel(String designationLabel) {
        this.designationLabel = designationLabel;
        return this;
    }

    public TeamMemberResposne createTeamMemberResposne() {
        return new TeamMemberResposne(teamID, teamName, id, firstName, lastName, email, gender, ethnicity, address, phoneNumber, bankName, accountNumber, photo, designation, dateOfBirth, contractStart, contractEnd, bank,IDPassDID,designationLabel);
    }
}