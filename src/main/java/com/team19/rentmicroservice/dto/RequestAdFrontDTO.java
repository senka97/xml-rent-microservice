package com.team19.rentmicroservice.dto;

import com.team19.rentmicroservice.model.RequestAd;

import java.time.LocalDate;

public class RequestAdFrontDTO {

    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private double payment;
    private double currentPricePerKm;
    private AdFrontDTO ad;

    public RequestAdFrontDTO(){

    }

    public RequestAdFrontDTO(RequestAd requestAd){
        this.id = requestAd.getId();
        this.startDate = requestAd.getStartDate();
        this.endDate = requestAd.getEndDate();
        this.payment = requestAd.getPayment();
        this.currentPricePerKm = requestAd.getCurrentPricePerKm();
        this.ad = new AdFrontDTO(requestAd.getAdID());
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

    public double getPayment() {
        return payment;
    }

    public void setPayment(double payment) {
        this.payment = payment;
    }

    public double getCurrentPricePerKm() {
        return currentPricePerKm;
    }

    public void setCurrentPricePerKm(double currentPricePerKm) {
        this.currentPricePerKm = currentPricePerKm;
    }

    public AdFrontDTO getAd() {
        return ad;
    }

    public void setAd(AdFrontDTO ad) {
        this.ad = ad;
    }
}
