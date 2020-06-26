package com.team19.rentmicroservice.model;

import com.github.rkpunjal.sqlsafe.SQLInjectionSafe;

import javax.persistence.*;

@Entity
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="content")
    private @SQLInjectionSafe String content;

    @Column(name="km")
    private double km;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "request_ad_id", referencedColumnName = "id")
    private RequestAd requestAd;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "reservation_id", referencedColumnName = "id")
    private Reservation reservation;

    public Report()
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

    public RequestAd getRequestAd() {
        return requestAd;
    }

    public void setRequestAd(RequestAd requestAd) {
        this.requestAd = requestAd;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }
}
