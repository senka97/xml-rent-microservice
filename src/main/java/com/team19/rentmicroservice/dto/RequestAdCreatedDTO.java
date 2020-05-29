package com.team19.rentmicroservice.dto;

import com.team19.rentmicroservice.model.RequestAd;

import java.time.LocalDate;

public class RequestAdCreatedDTO {

    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private double currentPricePerKm;
    private double payment;
    private Long clientID;
    private Long ownerID;
    private Long adID;

    public RequestAdCreatedDTO(){

    }

    public RequestAdCreatedDTO(RequestAd requestAd){
        this.id = requestAd.getId();
        this.startDate = requestAd.getStartDate();
        this.endDate = requestAd.getEndDate();
        this.currentPricePerKm = requestAd.getCurrentPricePerKm();
        this.payment = requestAd.getPayment();
        this.clientID = requestAd.getClientID();
        this.ownerID = requestAd.getOwnerID();
        this.adID = requestAd.getAdID();
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

    public double getPayment() {
        return payment;
    }

    public void setPayment(double payment) {
        this.payment = payment;
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
}
