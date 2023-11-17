package de.hs.da.hskleinanzeigen.entity;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "USER")
public class User {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private int id;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "PASSWORD", nullable = false)
    private String password;
    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "LOCATION")
    private String location;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED", nullable = false)
    private Date created;

    @OneToMany(mappedBy = "user")
    private List<Advertisement> advertisement;

    @PrePersist
    protected void onCreate() {
        created = new Date();
    }

    public User(String email, String password, String firstname, String lastname,String phone_number, String location) {
        this.email = email;
        this.password = password;
        this.firstName = firstname;
        this.lastName = lastname;
        this.location = location;
        this.phone = phone_number;
    }

    public User() {

    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public String getLocation() {
        return location;
    }
}
