package com.team19.rentmicroservice.dto;

public class PriceListAdDTO {

    private Long adID;
    private double pricePerKm;
    private double pricePerDay;
    private int discount20Days;
    private int discount30Days;

    public PriceListAdDTO(){

    }

    public PriceListAdDTO(Long adID){
        this.adID = adID;
        this.pricePerKm = 0;
        this.pricePerDay = 0;
        this.discount20Days = 0;
        this.discount30Days = 0;
    }

    public Long getAdID() {
        return adID;
    }

    public void setAdID(Long adID) {
        this.adID = adID;
    }

    public double getPricePerKm() {
        return pricePerKm;
    }

    public void setPricePerKm(double pricePerKm) {
        this.pricePerKm = pricePerKm;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public int getDiscount20Days() {
        return discount20Days;
    }

    public void setDiscount20Days(int discount20Days) {
        this.discount20Days = discount20Days;
    }

    public int getDiscount30Days() {
        return discount30Days;
    }

    public void setDiscount30Days(int discount30Days) {
        this.discount30Days = discount30Days;
    }
}
