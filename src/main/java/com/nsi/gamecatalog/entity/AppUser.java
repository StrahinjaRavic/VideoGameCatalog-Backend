package com.nsi.gamecatalog.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "app_user")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, length = 200)
    private String address;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, unique = true, length = 30)
    private String phone;

    @Column(nullable = false)
    private String password;

    public AppUser() {}

    public AppUser(String firstName, String lastName, String address, String city,
                   String email, String phone, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
