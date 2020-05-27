package com.team19.rentmicroservice.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="inCart")
    private boolean inCart; //kad se posalje zahtev brise se iz carta, a moze i pre toga da se obrise
    @Column(name="adID")
    private Long adID;
    @Column(name="startDate")
    private LocalDate startDate;
    @Column(name="endDate")
    private LocalDate endDate;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Cart cart;

    public CartItem(){

    }
}
