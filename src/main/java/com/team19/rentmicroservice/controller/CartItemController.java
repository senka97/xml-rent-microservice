package com.team19.rentmicroservice.controller;

import com.team19.rentmicroservice.client.AdClient;
import com.team19.rentmicroservice.dto.AdOwnerDTO;
import com.team19.rentmicroservice.dto.CartItemRequestDTO;
import com.team19.rentmicroservice.dto.CartItemResponseDTO;
import com.team19.rentmicroservice.security.CustomPrincipal;
import com.team19.rentmicroservice.service.impl.CartItemServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping(value = "/api/cartItem", produces = MediaType.APPLICATION_JSON_VALUE)
public class CartItemController {

    @Autowired
    private CartItemServiceImpl cartItemService;
    @Autowired
    private AdClient adClient;

    Logger logger = LoggerFactory.getLogger(CartItemController.class);

    @PostMapping(consumes = "application/json")
    @PreAuthorize("hasAuthority('cartItem_insert')")
    public ResponseEntity<?> addCartItem(@RequestBody CartItemRequestDTO cartItemRequestDTO){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();

        String msg = cartItemService.validateCartItemRequest(cartItemRequestDTO);
        if(msg != null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }

        //proveri se da li takav oglas postoji u ad-service i vrati se id vlasnika oglasa i kada je oglas aktivan
        AdOwnerDTO adOwnerDTO = adClient.getAdOwner(cartItemRequestDTO.getAdID(),cp.getPermissions(),cp.getUserID(),cp.getToken());
        if(adOwnerDTO == null){
            logger.warn(MessageFormat.format("AdID:{0}-not found;UserID:{1}", cartItemRequestDTO.getAdID(), cp.getUserID()));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This ad doesn't exist.");
        }else{
            //ako oglas postoji proveri se da li je klijent isti kao i vlasnik oglasa
            if(adOwnerDTO.getOwnerID() == Long.parseLong(cp.getUserID())){
                logger.warn(MessageFormat.format("AdID:{0}-not user's ad;UserID:{1}", cartItemRequestDTO.getAdID(), cp.getUserID()));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You can't add your own ad in your cart.");
            }else{ //ako klijent nije isti kao vlasnik proveri da li je oglas aktivan u tom periodu
                if(cartItemRequestDTO.getStartDate().isBefore(adOwnerDTO.getStartDate()) || cartItemRequestDTO.getEndDate().isAfter(adOwnerDTO.getEndDate())){
                    logger.warn(MessageFormat.format("AdID:{0}-not active;UserID:{1}", cartItemRequestDTO.getAdID(), cp.getUserID()));
                    String startDateFormatted = cartItemRequestDTO.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    String endDateFormatted = cartItemRequestDTO.getEndDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This ad is not active for the last searched dates: " + startDateFormatted + " to " + endDateFormatted + ".");
                }
            }
        }
        CartItemResponseDTO cartItemResponseDTO = cartItemService.addCartItem(cartItemRequestDTO, adOwnerDTO.getOwnerID());
        if(cartItemResponseDTO == null){
            logger.warn(MessageFormat.format("AdID:{0}-already in the cart;UserID:{1}", cartItemRequestDTO.getAdID(), cp.getUserID()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This ad for this period is already in the cart or this action is disabled for you.");
        }
        logger.info(MessageFormat.format("CI-ID:{0}-created;UserID:{1}", cartItemResponseDTO.getId(), cp.getUserID())); //CI-Cart item
        return new ResponseEntity(cartItemResponseDTO, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('cartItem_delete')")
    public ResponseEntity<?> deleteCartItem(@PathVariable Long id){

         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
         CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();
         boolean deleted = cartItemService.deleteCartItem(id);
         if(deleted){
             logger.info(MessageFormat.format("CI-ID:{0}-deleted;UserID:{1}", id, cp.getUserID())); //CI-Cart item
             return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
         }else{
             logger.warn(MessageFormat.format("CI-ID:{0}-not found;UserID:{1}", id, cp.getUserID()));
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart item doesn't exist.");
         }
    }


}
