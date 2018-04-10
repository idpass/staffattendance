package np.com.naxa.staffattendance.pojo;

/**
 * Created by user on 4/1/2018.
 */

public class Staff {

    String name;
    String staffType;
    String createdBy;
    String createdAt;
    String updatedAt;

    public Staff(String name, String staffType) {
        this.name = name;
        this.staffType = staffType;
    }

    public Staff(String name, String staffType, String createdBy, String createdAt, String updatedAt) {
        this.name = name;
        this.staffType = staffType;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStaffType() {
        return staffType;
    }

    public void setStaffType(String staffType) {
        this.staffType = staffType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Staff staff = (Staff) o;

        if (name != null ? !name.equals(staff.name) : staff.name != null) return false;
        if (staffType != null ? !staffType.equals(staff.staffType) : staff.staffType != null)
            return false;
        if (createdBy != null ? !createdBy.equals(staff.createdBy) : staff.createdBy != null)
            return false;
        if (createdAt != null ? !createdAt.equals(staff.createdAt) : staff.createdAt != null)
            return false;
        return updatedAt != null ? updatedAt.equals(staff.updatedAt) : staff.updatedAt == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (staffType != null ? staffType.hashCode() : 0);
        result = 31 * result + (createdBy != null ? createdBy.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Staff{" +
                "name='" + name + '\'' +
                ", staffType='" + staffType + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
