package com.team19.rentmicroservice.dto;

import java.time.LocalDate;

public class AdDTOSimple {

    private Long id;

    private LocalDate startDate;

    private LocalDate endDate;

    public AdDTOSimple()
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
}
