package com.team19.rentmicroservice.controller;

import com.team19.rentmicroservice.dto.RentRequestDTO;
import com.team19.rentmicroservice.dto.RequestCreatedDTO;
import com.team19.rentmicroservice.dto.RequestFrontDTO;
import com.team19.rentmicroservice.enums.RequestStatus;
import com.team19.rentmicroservice.model.Request;
import com.team19.rentmicroservice.security.CustomPrincipal;
import com.team19.rentmicroservice.service.impl.CartServiceImpl;
import com.team19.rentmicroservice.service.impl.RequestServiceImpl;
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
import java.util.List;

@RestController
@RequestMapping(value = "/api/request", produces = MediaType.APPLICATION_JSON_VALUE)
public class RequestController {

    @Autowired
    private RequestServiceImpl requestService;

    @Autowired
    private CartServiceImpl cartService;

    Logger logger = LoggerFactory.getLogger(RequestController.class);

    @PostMapping(consumes = "application/json")
    @PreAuthorize("hasAuthority('request_create')")
    public ResponseEntity<?> createRequests(@RequestBody RentRequestDTO rentRequestDTO){

         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
         CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();
         ResponseEntity<?> response =  this.cartService.validateCart(rentRequestDTO.getCartItemsIDs());
         if(response != null){
             return  response;
         }
         List<RequestCreatedDTO> requestCreatedDTOs = this.requestService.createRequests(rentRequestDTO);
         for(RequestCreatedDTO r: requestCreatedDTOs) {
             logger.info(MessageFormat.format("R-ID:{0}-created;UserID:{1}",r.getId(), cp.getUserID())); //R-request
         }
        return new ResponseEntity(requestCreatedDTOs, HttpStatus.CREATED);
    }


    @PutMapping(value="/accept/{id}")
    @PreAuthorize("hasAuthority('request_update')")
    public ResponseEntity<?> acceptRequest(@PathVariable("id") Long id){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();
        Request request = this.requestService.findOne(id);
        if(request == null){
            logger.warn(MessageFormat.format("R-ID:{0}-NF-AF;UserID:{1}", id, cp.getUserID())); //NF-not found, AF-accept failed
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Request with with that id doesn't exist.");
        }else{
            if(request.getStatus() != RequestStatus.Pending){
                logger.warn(MessageFormat.format("R-ID:{0}-NP-AF;UserID:{1}", id, cp.getUserID())); //NP-not pending, AF-accept failed
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This request doesn't have pending status and it can't be accepted.");
            }else{
                //proverim da li je ovo zahtev za oglas koji pripada ulogovanom korisniku
                if(request.getOwnerID() != Long.parseLong(cp.getUserID())){
                    logger.warn(MessageFormat.format("R-ID:{0}-NUR-AF;UserID:{1}", id, cp.getUserID())); //NUR-not user's request, AF-accept failed
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This is not request for your ad, so you can't change its status.");
                }else {
                    String msg;
                    msg = this.requestService.acceptRequest(request);
                    if (msg != null) {
                        logger.info(MessageFormat.format("R-ID:{0}-AR-AF;UserID:{1}", id, cp.getUserID())); //AR-already reserved, AF-accept failed, informacija
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
                    } else {
                        logger.info(MessageFormat.format("R-ID:{0}-accepted;UserID:{1}", id, cp.getUserID()));
                        return ResponseEntity.status(HttpStatus.OK).build();
                    }
                }
            }
        }
    }

    @PutMapping(value="/reject/{id}")
    @PreAuthorize("hasAuthority('request_update')")
    public ResponseEntity<?> rejectRequest(@PathVariable("id") Long id){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();

        Request request = this.requestService.findOne(id);
        if(request == null){
            logger.warn(MessageFormat.format("R-ID:{0}-NF-RF;UserID:{1}", id, cp.getUserID())); //NF-not found, RF-reject failed
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Request with with that id doesn't exist.");
        }else{
            if(request.getStatus() != RequestStatus.Pending){
                logger.warn(MessageFormat.format("R-ID:{0}-NP-RF;UserID:{1}", id, cp.getUserID())); //NP-not pending, RF-reject failed
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This request doesn't have pending status and it can't be rejected.");
            }else{
                //proverim da li je ovo zahtev za oglas koji pripada ulogovanom korisniku
                if(request.getOwnerID() != Long.parseLong(cp.getUserID())){
                    logger.warn(MessageFormat.format("R-ID:{0}-NUR-RF;UserID:{1}", id, cp.getUserID())); //NUR-not user's request, RF-reject failed
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This is not request for your ad, so you can't change its status.");
                }else {
                    this.requestService.rejectRequest(request);
                    logger.info(MessageFormat.format("R-ID:{0}-rejected;UserID:{1}", id, cp.getUserID()));
                    return ResponseEntity.status(HttpStatus.OK).build();
                }
            }
        }
    }
    @PutMapping(value="/cancel/{id}")
    @PreAuthorize("hasAuthority('request_update_cancel')")
    public ResponseEntity<?> cancelRequest(@PathVariable("id") Long id){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();

        Request request = this.requestService.findOne(id);
        if(request == null){
            logger.warn(MessageFormat.format("R-ID:{0}-NF-CF;UserID:{1}", id, cp.getUserID())); //NF-not found, CF-cancel failed
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Request with with that id doesn't exist.");
        }else{
            if(request.getStatus() != RequestStatus.Pending){
                logger.warn(MessageFormat.format("R-ID:{0}-NP-CF;UserID:{1}", id, cp.getUserID())); //NP-not pending, CF-cancel failed
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This request doesn't have pending status and it can't be canceled.");
            }else{
                //proverim da li je ovaj zahtev kreiran od strane ulogovanog korisnika
                if(request.getClientID() != Long.parseLong(cp.getUserID())){
                    logger.warn(MessageFormat.format("R-ID:{0}-NUR-CF;UserID:{1}", id, cp.getUserID())); //NUR-not user's request, CF-cancel failed
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This is not your request, so you can't change its status.");
                }else {
                    this.requestService.cancelRequest(request);
                    logger.info(MessageFormat.format("R-ID:{0}-canceled;UserID:{1}", id, cp.getUserID()));
                    return ResponseEntity.status(HttpStatus.OK).build();
                }
            }
        }
    }

    @GetMapping(value="/pending")
    @PreAuthorize("hasAuthority('request_read')")
    public ResponseEntity<?> getPendingRequestsFront(){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();
        logger.info(MessageFormat.format("PER-read;UserID:{1}", cp.getUserID())); //PER-pending requests

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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();
        logger.info(MessageFormat.format("PAR-read;UserID:{1}", cp.getUserID())); //PAR-paid requests

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
    @PreAuthorize("hasAuthority('request_update')")
    public ResponseEntity<?> rejectAllPendingRequestsForBlockedOrRemovedClient(@PathVariable Long id) {
        requestService.rejectAllPendingRequestsForBlockedOrRemovedClient(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
