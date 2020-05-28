package com.team19.rentmicroservice.service;

import com.team19.rentmicroservice.dto.CartItemDTO;
import com.team19.rentmicroservice.model.Cart;

import java.util.List;

public interface CartService {

      List<CartItemDTO> getCart();
}
