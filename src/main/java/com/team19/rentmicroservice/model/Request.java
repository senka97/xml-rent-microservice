package com.team19.rentmicroservice.model;

import com.team19.rentmicroservice.enums.RequestStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Request {
    //ovo je klasa za bundle koja sadrzi jedan ili vise zahteva za oglase od istog vlasnika
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private RequestStatus status;
    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, fetch= FetchType.LAZY)
    private Set<RequestAd> requestAds; //mora CascadeType.ALL jer cuvam ih sve odjednom u bazu
    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Message> messages;
    @Column(name="ownerID")
    private Long ownerID; //ko je vlasnik oglasa
    @Column(name="clientID")
    private Long clientID; //ko je iznajmio oglas/e
    @Column(name="creationTime")
    private LocalDateTime creationTime;

    public Request(){

    }

    public Request(HashSet<RequestAd> requestAds, Long ownerID, Long clientID){
        this.status = RequestStatus.Pending;
        this.requestAds = requestAds;
        this.messages = new HashSet<>();
        this.ownerID = ownerID;
        this.clientID= clientID;
        this.creationTime = LocalDateTime.now();
    }

    public Request(Long ownerID, Long clientID){
        this.status = RequestStatus.Pending;
        this.requestAds = new HashSet<>();
        this.messages = new HashSet<>();
        this.ownerID = ownerID;
        this.clientID= clientID;
        this.creationTime = LocalDateTime.now();

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public Set<RequestAd> getRequestAds() {
        return requestAds;
    }

    public void setRequestAds(Set<RequestAd> requestAds) {
        this.requestAds = requestAds;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }

    public Long getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(Long ownerID) {
        this.ownerID = ownerID;
    }

    public Long getClientID() {
        return clientID;
    }

    public void setClientID(Long clientID) {
        this.clientID = clientID;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }
}
