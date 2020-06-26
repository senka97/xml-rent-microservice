package com.team19.rentmicroservice.model;

import com.github.rkpunjal.sqlsafe.SQLInjectionSafe;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="content")
    private @SQLInjectionSafe String content;
    @Column(name="dateTime")
    private LocalDateTime dateTime;
    //@Column(name="fromUserID")
    //private Long fromUserID;
    //@Column(name="fromUserInfo")
    //private String fromUserInfo;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserInfo fromUserInfo;
    //@Column(name="toUserID")
    //private Long toUserID;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Request request;

    public Message(){

    }

    public Message(String content, LocalDateTime dateTime, UserInfo fromUserInfo, Request request){
        this.content = content;
        this.dateTime = dateTime;
        this.fromUserInfo = fromUserInfo;
        this.request = request;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public UserInfo getFromUserInfo() {
        return fromUserInfo;
    }

    public void setFromUserInfo(UserInfo fromUserInfo) {
        this.fromUserInfo = fromUserInfo;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }
}
