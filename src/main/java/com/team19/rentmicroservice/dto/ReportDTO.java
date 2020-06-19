package com.team19.rentmicroservice.dto;


public class ReportDTO {

    private Long id;

    private String content;

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
