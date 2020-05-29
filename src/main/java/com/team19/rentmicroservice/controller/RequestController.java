package com.team19.rentmicroservice.controller;

import com.team19.rentmicroservice.dto.RentRequestDTO;
import com.team19.rentmicroservice.dto.RequestCreatedDTO;
import com.team19.rentmicroservice.service.impl.CartServiceImpl;
import com.team19.rentmicroservice.service.impl.RequestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/request", produces = MediaType.APPLICATION_JSON_VALUE)
public class RequestController {

    @Autowired
    private RequestServiceImpl requestService;

    @Autowired
    private CartServiceImpl cartService;

    @PostMapping(consumes = "application/json")
    @PreAuthorize("hasAuthority('request_insert')")
    public ResponseEntity<?> createRequests(@RequestBody RentRequestDTO rentRequestDTO){

         String msg = this.cartService.validateCart(rentRequestDTO.getCartItemsIDs());
         if(msg != null){
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
         }
        List<RequestCreatedDTO> requestCreatedDTOs = this.requestService.createRequests(rentRequestDTO);
         return new ResponseEntity(requestCreatedDTOs, HttpStatus.CREATED);
    }
}
