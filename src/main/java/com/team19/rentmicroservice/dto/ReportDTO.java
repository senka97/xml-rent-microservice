package com.team19.rentmicroservice.dto;

import javax.validation.constraints.*;

public class ReportDTO {

    private Long id;

    @NotEmpty(message="Content must not be null or empty.")
    @Size(min=2, message = "Content length must be 2 characters minimum")
    @Pattern(regexp="^[a-zA-Z0-9.,?! ]*$", message="Content must not include special characters.")
    private String content;

    @NotNull(message = "Number of km must not be null")
    @Positive(message="Number of km must be positive number.")
    private double km;

    private Long requestAdId;

    private Long reservationId;

    public ReportDTO()
    {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getKm() {
        return km;
    }

    public void setKm(double km) {
        this.km = km;
    }

    public Long getRequestAdId() {
        return requestAdId;
    }

    public void setRequestAdId(Long requestAdId) {
        this.requestAdId = requestAdId;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }
}
