package com.team19.rentmicroservice.model;

import com.team19.rentmicroservice.enums.RequestStatus;

import javax.persistence.*;
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
    private Set<RequestAd> requestAds;
    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Message> messages;

    public Request(){

    }
}
