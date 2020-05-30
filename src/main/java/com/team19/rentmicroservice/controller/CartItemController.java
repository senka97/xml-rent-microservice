package com.team19.rentmicroservice.controller;

import com.team19.rentmicroservice.client.AdClient;
import com.team19.rentmicroservice.dto.CartItemRequestDTO;
import com.team19.rentmicroservice.dto.CartItemResponseDTO;
import com.team19.rentmicroservice.security.CustomPrincipal;
import com.team19.rentmicroservice.service.impl.CartItemServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/cartItem", produces = MediaType.APPLICATION_JSON_VALUE)
public class CartItemController {

    @Autowired
    private CartItemServiceImpl cartItemService;
    @Autowired
    private AdClient adClient;

    @PostMapping(consumes = "application/json")
    @PreAuthorize("hasAuthority('cartItem_insert')")
    public ResponseEntity<?> addCartItem(@RequestBody CartItemRequestDTO cartItemRequestDTO){

        String msg = cartItemService.validateCartItemRequest(cartItemRequestDTO);
        if(msg != null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();
        //proveri se da li takav oglas postoji u ad-service i vrati se id vlasnika oglasa
        Long owner = adClient.getAdOwner(cartItemRequestDTO.getAdID(),cp.getPermissions(),cp.getUserID(),cp.getToken());
        if(owner == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This ad doesn't exist.");
        }else{
            //ako oglas postoji proveri se da li je klijent isti kao i vlasnik oglasa
            if(owner == Long.parseLong(cp.getUserID())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You can't add your own ad in your cart.");
            }
        }
        CartItemResponseDTO cartItemResponseDTO = cartItemService.addCartItem(cartItemRequestDTO, owner);
        if(cartItemResponseDTO == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This ad for this period is already in the cart.");
        }
        return new ResponseEntity(cartItemResponseDTO, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('cartItem_delete')")
    public ResponseEntity<?> deleteCartItem(@PathVariable Long id){

         boolean deleted = cartItemService.deleteCartItem(id);
         if(deleted){
             return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
         }else{
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart item doesn't exist.");
         }
    }


}
