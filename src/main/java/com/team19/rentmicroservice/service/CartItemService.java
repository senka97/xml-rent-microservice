package com.team19.rentmicroservice.service;

import com.team19.rentmicroservice.dto.CartItemRequestDTO;
import com.team19.rentmicroservice.dto.CartItemResponseDTO;
import com.team19.rentmicroservice.model.Cart;
import com.team19.rentmicroservice.model.CartItem;

import java.util.List;

public interface CartItemService {

    String validateCartItemRequest(CartItemRequestDTO cartItemRequestDTO);
    CartItemResponseDTO addCartItem(CartItemRequestDTO cartItemRequestDTO,Long ownerID);
    boolean deleteCartItem(Long id);
}
