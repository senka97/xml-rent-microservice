package com.team19.rentmicroservice.dto;

import java.time.LocalDate;

public class CartItemRequestDTO {

    private Long adID;
    private LocalDate startDate;
    private LocalDate endDate;

    public CartItemRequestDTO(){

    }

    public CartItemRequestDTO(Long adID, LocalDate startDate, LocalDate endDate){
        this.adID = adID;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getAdID() {
        return adID;
    }

    public void setAdID(Long adID) {
        this.adID = adID;
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
}
