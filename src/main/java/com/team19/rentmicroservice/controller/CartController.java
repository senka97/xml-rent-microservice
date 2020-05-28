package com.team19.rentmicroservice.controller;

import com.team19.rentmicroservice.service.impl.CartServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/cart", produces = MediaType.APPLICATION_JSON_VALUE)
public class CartController {

    @Autowired
    private CartServiceImpl cartService;

    @GetMapping
    @PreAuthorize("hasAuthority('cart_read')")
    public ResponseEntity<?> getCart(){

        return new ResponseEntity<>(cartService.getCart(), HttpStatus.OK);
    }

}
