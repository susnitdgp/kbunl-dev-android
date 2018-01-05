package kbunl.com.kbunl_dev.model;

public class Employee {

    private String email;

    private String employeeName;
    private String employeeNumber;

    private ItComplainRoles roles;


    public Employee(){
        roles=new ItComplainRoles();
    }

    //Getter and Setter
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public ItComplainRoles getRoles() {
        return roles;
    }

    public void setRoles(ItComplainRoles roles) {
        this.roles = roles;
    }
}
