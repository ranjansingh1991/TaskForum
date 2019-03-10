package in.enzen.taskforum.model;

/**
 * Created by Rupesh on 01-12-2017.
 */

public class AccountBean {

    private String name = "";
    private String email = "";
    private String image_url = "";
    private String birthday = "";
    private String phone = "";
    private String gender = "";
    private String address = "";

    private String employer = "";
    private String empID = "";
    private String designation = "";
    private String rep_manager = "";

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageURL() {
        return image_url;
    }

    public void setImageURL(String image_url) {
        this.image_url = image_url;
    }

    public String getEmployer() {
        return employer;
    }

    public void setEmployer(String employer) {
        this.employer = employer;
    }

    public String getEmpID() {
        return empID;
    }

    public void setEmpID(String empID) {
        this.empID = empID;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getRep_manager() {
        return rep_manager;
    }

    public void setRep_manager(String rep_manager) {
        this.rep_manager = rep_manager;
    }
}
