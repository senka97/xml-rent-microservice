package com.team19.rentmicroservice.service.impl;

import com.team19.rentmicroservice.client.AdClient;
import com.team19.rentmicroservice.dto.CartItemRequestDTO;
import com.team19.rentmicroservice.dto.CartItemResponseDTO;
import com.team19.rentmicroservice.model.Cart;
import com.team19.rentmicroservice.model.CartItem;
import com.team19.rentmicroservice.repository.CartItemRepository;
import com.team19.rentmicroservice.repository.CartRepository;
import com.team19.rentmicroservice.security.CustomPrincipal;
import com.team19.rentmicroservice.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CartItemServiceImpl implements CartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private AdClient adClient;

    @Override
    public String validateCartItemRequest(CartItemRequestDTO cartItemRequestDTO) {
        String msg = "";
        boolean valid = true;
        if(cartItemRequestDTO.getAdID() == null || cartItemRequestDTO.getAdID()<1){
            msg += "Advertisement id is mandatory and it has to be a positive number. ";
            valid = false;
        }
        if(cartItemRequestDTO.getStartDate() == null || cartItemRequestDTO.getEndDate() == null){
            msg += "Start date and end date are mandatory.";
            valid = false;
        }else{
            if(cartItemRequestDTO.getStartDate().isAfter(cartItemRequestDTO.getEndDate())){
                msg += "Start date has to be before end date.";
                valid = false;
            }
        }
        if(!valid){
            return msg;
        }
        return null;
    }

    @Override
    public CartItemResponseDTO addCartItem(CartItemRequestDTO cartItemRequestDTO, Long ownerID) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();
        Cart cart = cartRepository.findByClientID(Long.parseLong(cp.getUserID()));
        //ako za tog korisnika ne postoji cart napravi se
        if(cart == null){
            Cart newCart = new Cart(Long.parseLong(cp.getUserID()));
            cartRepository.save(newCart);
            cart = cartRepository.findByClientID(Long.parseLong(cp.getUserID()));
        }
        //proverim da li postoji takav cartItem vec u cart-u
        CartItem cartItem = cartItemRepository.findByAdIDAndStartDateAndEndDateAndCart(cartItemRequestDTO.getAdID(),
                cartItemRequestDTO.getStartDate(),cartItemRequestDTO.getEndDate(),cart);
        //ako ne postoji napravim novi
        if(cartItem == null) {
            cartItem = new CartItem(cartItemRequestDTO.getAdID(), cartItemRequestDTO.getStartDate(), cartItemRequestDTO.getEndDate(), ownerID);
            cartItem.setCart(cart);
        }else{ //ako fizicki postoji u korpi
            if(cartItem.isInCart()){ //proverim da li je i logicki u korpi
                return null; //ako je vec u korpi vratim null
            }
            cartItem.setInCart(true); //ako nije vratim je u korpu
        }
        cartItem = cartItemRepository.save(cartItem);

        return new CartItemResponseDTO(cartItem);
    }

    @Override
    public boolean deleteCartItem(Long id) {

        CartItem cartItem = cartItemRepository.findById(id).orElse(null);
        if(cartItem == null){
            return false;
        }
        cartItem.setInCart(false);
        cartItemRepository.save(cartItem);
        return true;
    }
}