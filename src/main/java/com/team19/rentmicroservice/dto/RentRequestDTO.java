package com.team19.rentmicroservice.dto;

import java.util.ArrayList;

public class RentRequestDTO {

    private ArrayList<Long> cartItemsIDs;
    boolean bundle;

    public RentRequestDTO(){

    }

    public RentRequestDTO(ArrayList<Long> cartItemsIDs, boolean budle){
        this.cartItemsIDs = cartItemsIDs;
        this.bundle = bundle;
    }

    public ArrayList<Long> getCartItemsIDs() {
        return cartItemsIDs;
    }

    public void setCartItemsIDs(ArrayList<Long> cartItemsIDs) {
        this.cartItemsIDs = cartItemsIDs;
    }

    public boolean isBundle() {
        return bundle;
    }

    public void setBundle(boolean bundle) {
        this.bundle = bundle;
    }
}
