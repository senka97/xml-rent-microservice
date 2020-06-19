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
    private double currentPricePerKm; //cena ako se prekorace km
    @Column(name="payment")
    private double payment; //ukupna kolicina para
    @Column(name="clientID")
    private Long clientID; //ko je iznajmio oglas
    @Column(name="ownerID")
    private Long ownerID; //ko je vlasnik oglasa
    @Column(name="adID")
    private Long adID;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Request request;

    @OneToOne(mappedBy = "requestAd")
    private Report report;

    @Column(name = "reportCreated")
    private Boolean reportCreated;

    public RequestAd(){

    }

    public RequestAd(CartItem cartItem, Long clientID){
        this.startDate = cartItem.getStartDate();
        this.endDate = cartItem.getEndDate();
        this.clientID = clientID;
        this.ownerID = cartItem.getOwnerID();
        this.adID = cartItem.getAdID();
        this.currentPricePerKm = 0; //treba dobaviti iz car-microservice
        this.payment = 0;
        this.reportCreated = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public double getCurrentPricePerKm() {
        return currentPricePerKm;
    }

    public void setCurrentPricePerKm(double currentPricePerKm) {
        this.currentPricePerKm = currentPricePerKm;
    }

    public Long getClientID() {
        return clientID;
    }

    public void setClientID(Long clientID) {
        this.clientID = clientID;
    }

    public Long getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(Long ownerID) {
        this.ownerID = ownerID;
    }

    public Long getAdID() {
        return adID;
    }

    public void setAdID(Long adID) {
        this.adID = adID;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public double getPayment() {
        return payment;
    }

    public void setPayment(double payment) {
        this.payment = payment;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public Boolean getReportCreated() {
        return reportCreated;
    }

    public void setReportCreated(Boolean reportCreated) {
        this.reportCreated = reportCreated;
    }
}
