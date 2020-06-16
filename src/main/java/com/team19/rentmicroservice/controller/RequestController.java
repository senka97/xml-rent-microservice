package com.team19.rentmicroservice.controller;

import com.team19.rentmicroservice.dto.RentRequestDTO;
import com.team19.rentmicroservice.dto.RequestCreatedDTO;
import com.team19.rentmicroservice.dto.RequestFrontDTO;
import com.team19.rentmicroservice.enums.RequestStatus;
import com.team19.rentmicroservice.model.Request;
import com.team19.rentmicroservice.security.CustomPrincipal;
import com.team19.rentmicroservice.service.impl.CartServiceImpl;
import com.team19.rentmicroservice.service.impl.RequestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/request", produces = MediaType.APPLICATION_JSON_VALUE)
public class RequestController {

    @Autowired
    private RequestServiceImpl requestService;

    @Autowired
    private CartServiceImpl cartService;

    @PostMapping(consumes = "application/json")
    @PreAuthorize("hasAuthority('request_create')")
    public ResponseEntity<?> createRequests(@RequestBody RentRequestDTO rentRequestDTO){

         ResponseEntity<?> response =  this.cartService.validateCart(rentRequestDTO.getCartItemsIDs());
         if(response != null){
             return  response;
         }
        List<RequestCreatedDTO> requestCreatedDTOs = this.requestService.createRequests(rentRequestDTO);
         return new ResponseEntity(requestCreatedDTOs, HttpStatus.CREATED);
    }


    @PutMapping(value="/accept/{id}")
    @PreAuthorize("hasAuthority('request_update')")
    public ResponseEntity<?> acceptRequest(@PathVariable("id") Long id){

        Request request = this.requestService.findOne(id);
        if(request == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Request with with that id doesn't exist.");
        }else{
            if(request.getStatus() != RequestStatus.Pending){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This request doesn't have pending status and it can't be accepted.");
            }else{
                //proverim da li je ovo zahtev za oglas koji pripada ulogovanom korisniku
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();
                if(request.getOwnerID() != Long.parseLong(cp.getUserID())){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This is not request for your ad, so you can't change its status.");
                }else {
                    String msg;
                    msg = this.requestService.acceptRequest(request);
                    if (msg != null) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
                    } else {
                        return ResponseEntity.status(HttpStatus.OK).build();
                    }
                }
            }
        }
    }

    @PutMapping(value="/reject/{id}")
    @PreAuthorize("hasAuthority('request_update')")
    public ResponseEntity<?> rejectRequest(@PathVariable("id") Long id){

        Request request = this.requestService.findOne(id);
        if(request == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Request with with that id doesn't exist.");
        }else{
            if(request.getStatus() != RequestStatus.Pending){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This request doesn't have pending status and it can't be rejected.");
            }else{
                //proverim da li je ovo zahtev za oglas koji pripada ulogovanom korisniku
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();
                if(request.getOwnerID() != Long.parseLong(cp.getUserID())){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This is not request for your ad, so you can't change its status.");
                }else {
                    this.requestService.rejectRequest(request);
                    return ResponseEntity.status(HttpStatus.OK).build();
                }
            }
        }
    }
    @PutMapping(value="/cancel/{id}")
    @PreAuthorize("hasAuthority('request_update_cancel')")
    public ResponseEntity<?> cancelRequest(@PathVariable("id") Long id){

        Request request = this.requestService.findOne(id);
        if(request == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Request with with that id doesn't exist.");
        }else{
            if(request.getStatus() != RequestStatus.Pending){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This request doesn't have pending status and it can't be canceled.");
            }else{
                //proverim da li je ovaj zahtev kreiran od strane ulogovanog korisnika
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();
                if(request.getClientID() != Long.parseLong(cp.getUserID())){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This is not your request, so you can't change its status.");
                }else {
                    this.requestService.rejectRequest(request);
                    return ResponseEntity.status(HttpStatus.OK).build();
                }
            }
        }
    }

    @GetMapping(value="/pending")
    @PreAuthorize("hasAuthority('request_read')")
    public ResponseEntity<?> getPendingRequestsFront(){

        List<RequestFrontDTO> requestFrontDTOs = this.requestService.getPendingRequestsFront();
        return new ResponseEntity(requestFrontDTOs,HttpStatus.OK);
    }

    @GetMapping(value="/pending/client/{id}")
    @PreAuthorize("hasAuthority('request_read')")
    public ResponseEntity<?> getPendingRequestsClientFront(@PathVariable("id") Long clientId){

        List<RequestFrontDTO> requestFrontDTOs = this.requestService.getPendingRequestsClientFront(clientId);
        return new ResponseEntity(requestFrontDTOs,HttpStatus.OK);
    }

    @GetMapping(value="/paid")
    @PreAuthorize("hasAuthority('request_read')")
    public ResponseEntity<?> getPaidRequestsFront(){

        List<RequestFrontDTO> requestFrontDTOs = this.requestService.getPaidRequestsFront();
        return new ResponseEntity(requestFrontDTOs,HttpStatus.OK);
    }

    @GetMapping(value="/paid/client/{id}")
    @PreAuthorize("hasAuthority('request_read')")
    public ResponseEntity<?> getPaidRequestsClientFront(@PathVariable("id") Long clientId){

        List<RequestFrontDTO> requestFrontDTOs = this.requestService.getPaidRequestsClientFront(clientId);
        return new ResponseEntity(requestFrontDTOs,HttpStatus.OK);
    }

    @GetMapping(value="/pending/number")
    @PreAuthorize("hasAuthority('request_read')")
    public ResponseEntity<?> getPendingRequestsNumber(){

        int num = this.requestService.getPendingRequestsNumber();
        return new ResponseEntity(num,HttpStatus.OK);
    }

    @PutMapping(value = "/client/{id}")
    @PreAuthorize("hasAuthority('request_reject_update')")
    public ResponseEntity<?> rejectAllPendingRequestsForBlockedOrRemovedClient(@PathVariable Long id) {
        requestService.rejectAllPendingRequestsForBlockedOrRemovedClient(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
