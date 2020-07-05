package com.team19.rentmicroservice.model;

import javax.persistence.*;

@Entity
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="payment")
    private Double payment;

    @Column(name="clientId")
    private Long clientId;

    @Column(name="paid")
    private Boolean paid;

    @Column(name="kmLimit")
    private Integer kmLimit;

    @Column(name="kmExceeded")
    private Double kmExceeded;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "request_ad_id", referencedColumnName = "id")
    private RequestAd requestAd;

    public Bill()
    {

    }

    public Bill(Double payment, Long clientId, RequestAd requestAd, Integer kmLimit, Double kmExceeded)
    {
        this.payment = payment;
        this.clientId= clientId;
        this.requestAd = requestAd;
        this.kmLimit = kmLimit;
        this.kmExceeded = kmExceeded;
        this.paid = false;
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

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public RequestAd getRequestAd() {
        return requestAd;
    }

    public void setRequestAd(RequestAd requestAd) {
        this.requestAd = requestAd;
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
}
