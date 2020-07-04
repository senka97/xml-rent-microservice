package com.team19.rentmicroservice.dto;

public class BillDTO {

    private Long id;

    private Long requestAdId;

    private Double payment;

    private Integer kmLimit;

    private Double kmExceeded;

    public BillDTO()
    {

    }

    public BillDTO(Long id, Long requestAdId, Double payment, Integer kmLimit, Double kmExceeded)
    {
        this.id = id;
        this.requestAdId = requestAdId;
        this.payment = payment;
        this.kmExceeded = kmExceeded;
        this.kmLimit = kmLimit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPayment() {
        return payment;
    }

    public void setPayment(Double payment) {
        this.payment = payment;
    }

    public Integer getKmLimit() {
        return kmLimit;
    }

    public void setKmLimit(Integer kmLimit) {
        this.kmLimit = kmLimit;
    }

    public Double getKmExceeded() {
        return kmExceeded;
    }

    public void setKmExceeded(Double kmExceeded) {
        this.kmExceeded = kmExceeded;
    }

    public Long getRequestAdId() {
        return requestAdId;
    }

    public void setRequestAdId(Long requestAdId) {
        this.requestAdId = requestAdId;
    }
}
