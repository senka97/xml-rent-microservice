package com.team19.rentmicroservice.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class RequestAd {
    //ovo je klasa za zahtev jednog oglasa
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="startDate")
    private LocalDate startDate;
    @Column(name="endDate")
    private LocalDate endDate;
    @Column(name="currentPricePerKm")
    private double currentPricePerKm;
    @Column(name="clientID")
    private Long clientID;
    @Column(name="adID")
    private Long adID;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Request request;

    public RequestAd(){

    }



}
