package com.team19.rentmicroservice.service;

import com.team19.rentmicroservice.dto.CartItemDTO;
import com.team19.rentmicroservice.model.Cart;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public interface CartService {

      List<CartItemDTO> getCart();
      ResponseEntity<?> validateCart(ArrayList<Long> cartItemsIDs);

}
