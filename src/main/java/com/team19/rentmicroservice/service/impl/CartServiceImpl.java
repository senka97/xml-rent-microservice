package com.team19.rentmicroservice.service.impl;


import com.team19.rentmicroservice.client.AdClient;
import com.team19.rentmicroservice.dto.CartItemDTO;
import com.team19.rentmicroservice.model.Cart;
import com.team19.rentmicroservice.model.CartItem;
import com.team19.rentmicroservice.repository.CartItemRepository;
import com.team19.rentmicroservice.repository.CartRepository;
import com.team19.rentmicroservice.security.CustomPrincipal;
import com.team19.rentmicroservice.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @Autowired
    private RequestAdServiceImpl requestAdService;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ReservationServiceImpl reservationService;


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

    @Override
    public ResponseEntity<?> validateCart(ArrayList<Long> cartItemsIDs) {
        //proverim da li u korpi od ulogovanog
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();
        Cart cart = cartRepository.findByClientID(Long.parseLong(cp.getUserID()));
        //proverim da li postoji cart da ulogovanu osobu
        if(cart == null){
            //return "This user doesn't have a cart and can't make requests.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This user doesn't have a cart and can't make requests.");
        }

        //proverim da li prosledjeni id od cart items postoje u korpi od ulogovanog
        for(Long cartItemId: cartItemsIDs){
            if(!cart.getCartItems().stream().filter(ci -> ci.getId() == cartItemId && ci.isInCart()).findFirst().isPresent()){
                //return "Invalid cart items. This user doesn't have these cart items in his/her cart.";
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid cart items. This user doesn't have these cart items in his/her cart.");
            }
        }

        //proverim da li su se ti oglasi u medjuvremenu zauzeli, pa da se ne pravi zahtev ako jesu
        //proverim da li je taj oglas zauzet u zahtevima ili u rezervacijama
        boolean exist = false;
        List<CartItem> inCart = this.cartItemRepository.findByCartAndInCart(cart,true);
        for(CartItem ci: inCart){
               if(this.requestAdService.checkIfAdReserved(ci.getAdID(),ci.getStartDate(),ci.getEndDate()) || this.reservationService.checkIfAdReserved(ci.getAdID(),ci.getStartDate(),ci.getEndDate())){
                   ci.setInCart(false);
                   exist = true;
               }
        }
        if(exist){
            cartRepository.save(cart);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Some of ads in you cart have been already reserved and they have been deleted from your cart. Please try again with the rest of ads.");
        }

        return null;
    }
}
