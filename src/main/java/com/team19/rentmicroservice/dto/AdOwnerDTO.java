package com.team19.rentmicroservice.dto;

import java.time.LocalDate;

public class AdOwnerDTO {

    private Long ownerID;
    private LocalDate startDate;
    private LocalDate endDate;

    public AdOwnerDTO(){

    }

    public Long getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(Long ownerID) {
        this.ownerID = ownerID;
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
