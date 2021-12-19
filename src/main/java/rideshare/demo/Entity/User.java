package rideshare.demo.Entity;

import javassist.bytecode.ByteArray;

import javax.persistence.Entity;
import javax.persistence.Lob;
import java.io.File;

/***
 *  This is the class that represents all the attributes of a user, the @entity annotation make the user class a table in the database with all it's attributes as columns. the class
 *  extends abstract entity so that it can get all the properties of the abstract entity e.g id, date created, date updated
 ***/
@Entity
public class User extends AbstractEntity {

    private String firstName;
    private String lastName;
    private String emailAddress;
    private String phoneNumber;
    private String password;
    private String status;
    private String rideStatus;
    private String role;
    private String username;
    private String actCode;

    /***
     *  This is the getter and setter methods is prevents the actual variables from being tampered with, thus instances are created for use
     ***/
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

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getActCode() {
        return actCode;
    }

    public void setActCode(String actCode) {
        this.actCode = actCode;
    }

    /***
     *  This is the toString method, it allows us to pass and view values in the format below
     ***/
    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", password='" + password + '\'' +
                ", status='" + status + '\'' +
                ", role='" + role + '\'' +
                ", username='" + username + '\'' +
                ", actCode='" + actCode + '\'' +
                '}';
    }
}
