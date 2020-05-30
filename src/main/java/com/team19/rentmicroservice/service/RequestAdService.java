package com.team19.rentmicroservice.service;

import com.team19.rentmicroservice.model.CartItem;

public interface RequestAdService {

    boolean checkIfAdReserved(CartItem cartItem);

}
