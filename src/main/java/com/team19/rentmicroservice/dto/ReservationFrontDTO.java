package com.team19.rentmicroservice.dto;

import java.time.LocalDate;

public class ReservationFrontDTO {

    private Long id;

    private LocalDate startDate;

    private LocalDate endDate;

    private String clientFirstName;

    private String clientLastName;

    private String clientEmail;

    private String clientPhoneNumber;

    private double currentPricePerKm;

    private double payment;

    private Long ownerID;

    private AdFrontDTO ad;

    public ReservationFrontDTO()
    {

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

    public String getClientFirstName() {
        return clientFirstName;
    }

    public void setClientFirstName(String clientFirstName) {
        this.clientFirstName = clientFirstName;
    }

    public String getClientLastName() {
        return clientLastName;
    }

    public void setClientLastName(String clientLastName) {
        this.clientLastName = clientLastName;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getClientPhoneNumber() {
        return clientPhoneNumber;
    }

    public void setClientPhoneNumber(String clientPhoneNumber) {
        this.clientPhoneNumber = clientPhoneNumber;
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

    public Long getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(Long ownerID) {
        this.ownerID = ownerID;
    }

    public AdFrontDTO getAd() {
        return ad;
    }

    public void setAd(AdFrontDTO ad) {
        this.ad = ad;
    }

}
