package com.team19.rentmicroservice.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="content")
    private String content;
    @Column(name="dateTime")
    private LocalDateTime dateTime;
    @Column(name="fromUserID")
    private Long fromUserID;
    @Column(name="toUserID")
    private Long toUserID;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Request request;

    public Message(){

    }

}
