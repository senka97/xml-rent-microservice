package com.team19.rentmicroservice.dto;

import com.team19.rentmicroservice.model.CartItem;

import java.time.LocalDate;

public class CartItemResponseDTO {

    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long adID;
    private Long ownerID;

    private CartItemResponseDTO(){

    }

    public CartItemResponseDTO(CartItem cartItem){
        this.id = cartItem.getId();
        this.startDate = cartItem.getStartDate();
        this.endDate = cartItem.getEndDate();
        this.adID = cartItem.getAdID();
        this.ownerID = cartItem.getOwnerID();
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

    public Long getAdID() {
        return adID;
    }

    public void setAdID(Long adID) {
        this.adID = adID;
    }

    public Long getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(Long ownerID) {
        this.ownerID = ownerID;
    }
}
