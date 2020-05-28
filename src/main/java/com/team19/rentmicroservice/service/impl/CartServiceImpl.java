package com.team19.rentmicroservice.service.impl;


import com.team19.rentmicroservice.client.AdClient;
import com.team19.rentmicroservice.dto.CartItemDTO;
import com.team19.rentmicroservice.model.Cart;
import com.team19.rentmicroservice.model.CartItem;
import com.team19.rentmicroservice.repository.CartRepository;
import com.team19.rentmicroservice.security.CustomPrincipal;
import com.team19.rentmicroservice.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private AdClient adClient;


    @Override
    public List<CartItemDTO> getCart() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();
        Cart cart = cartRepository.findByClientID(Long.parseLong(cp.getUserID()));
        List<CartItemDTO> cartItemDTOs = new ArrayList<>();
        if(cart == null) {
            return cartItemDTOs;
        }else{
            for(CartItem cartItem: cart.getCartItems()){
                if(cartItem.isInCart() && cartItem.getEndDate().isAfter(LocalDate.now())){
                    cartItemDTOs.add(new CartItemDTO(cartItem.getId(),cartItem.getStartDate(),cartItem.getEndDate(), cartItem.getAdID()));
                }
            }
            cartItemDTOs = adClient.findAds(cartItemDTOs,cp.getPermissions(),cp.getUserID(),cp.getToken());
            return cartItemDTOs;
        }
    }
}
