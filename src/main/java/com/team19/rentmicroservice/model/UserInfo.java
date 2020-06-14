package com.team19.rentmicroservice.model;

import com.team19.rentmicroservice.dto.UserInfoDTO;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="userId")
    private Long userId; //ovo je id iz user servisa
    @Column(name="name")
    private String name;
    @Column(name="surname")
    private String surname;
    @Column(name="email")
    private String email;
    @Column(name="role") //agent or client
    private String role;
    @Column(name="companyName") //ako je agent
    private String companyName;
    @OneToMany(mappedBy = "fromUserInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Message> messages;

    public UserInfo(){

    }

    public UserInfo(UserInfoDTO userInfoDTO){
        this.userId = userInfoDTO.getUserId();
        this.name = userInfoDTO.getName();
        this.surname = userInfoDTO.getSurname();
        this.email = userInfoDTO.getEmail();
        this.role = userInfoDTO.getRole();
        this.companyName = userInfoDTO.getCompanyName();
        this.messages = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }
}
