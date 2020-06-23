package com.team19.rentmicroservice.controller;

import com.team19.rentmicroservice.client.AdClient;
import com.team19.rentmicroservice.dto.AdOwnerDTO;
import com.team19.rentmicroservice.dto.RequestFrontDTO;
import com.team19.rentmicroservice.dto.ReservationDTO;
import com.team19.rentmicroservice.dto.ReservationFrontDTO;
import com.team19.rentmicroservice.model.Reservation;
import com.team19.rentmicroservice.security.CustomPrincipal;
import com.team19.rentmicroservice.service.impl.ReservationServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.List;

@RestController
@RequestMapping(value = "/api")
@CrossOrigin
public class ReservationController {

    @Autowired
    private ReservationServiceImpl reservationService;
    @Autowired
    private AdClient adClient;

    Logger logger = LoggerFactory.getLogger(RequestController.class);

    @PostMapping(value="/reservations",consumes="application/json")
    @PreAuthorize("hasAuthority('reservation_create')")
    public ResponseEntity<?> createNewReservation(@RequestBody ReservationDTO reservationDTO)  {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();

        if(reservationDTO.getClientFirstName() == null || reservationDTO.getClientLastName() == null || reservationDTO.getClientEmail() == null || reservationDTO.getClientPhoneNumber() == null){
            logger.warn("NRS-invalid:IC missing;UserID:" + cp.getUserID()); //NRS-new reservation, IC=information about client
            return new ResponseEntity<>("All information about client must be entered!", HttpStatus.BAD_REQUEST);
        }

        if(reservationDTO.getStartDate() == null || reservationDTO.getEndDate() == null){
            logger.warn("NRS-invalid:dates missing;UserID:" + cp.getUserID()); //NRS=new reservation
            return new ResponseEntity<>("Both start and end date must be selected",HttpStatus.BAD_REQUEST);
        }

        if(reservationDTO.getEndDate().isBefore(reservationDTO.getStartDate())){
            logger.warn("NRS-invalid:dates invalid;UserID:" + cp.getUserID()); //NRS=new reservation
            return new ResponseEntity<>("Start date must be before end date!",HttpStatus.BAD_REQUEST);
        }

        //dobavi se id vlasnika oglasa i proveri se da li je to oglas od trenutno ulogovanog
        AdOwnerDTO adOwnerDTO = adClient.getAdOwner(reservationDTO.getAdId(), cp.getPermissions(),cp.getUserID(),cp.getToken());

        if(adOwnerDTO == null){
            logger.warn(MessageFormat.format("NRS-invalid:adID {0} not found;UserID:{1}", reservationDTO.getAdId(), cp.getUserID())); //NRS=new reservation
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This ad doesn't exist.");
        }else{
            if(adOwnerDTO.getOwnerID() != Long.parseLong(cp.getUserID())){
                logger.warn(MessageFormat.format("NRS-invalid:adID {0} NUA;UserID:{1}", reservationDTO.getAdId(), cp.getUserID())); //NRS=new reservation, NUA=not user's ad
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You can't make a reservation for an ad that is not yours.");
            }
        }

        Reservation r = reservationService.createNewReservation(reservationDTO, adOwnerDTO.getOwnerID());

        if(r == null)
        {
            logger.warn(MessageFormat.format("NRS-failed:dates reserved;UserID:{1}", cp.getUserID())); //NRS=new reservation
            return new ResponseEntity<>("These dates are already reserved",HttpStatus.EXPECTATION_FAILED);
        }
        else return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value="/reservations")
    @PreAuthorize("hasAuthority('reservation_read')")
    public ResponseEntity<?> getReservationsFront(){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();
        logger.info("RS-read;UserID:" + cp.getUserID()); //RS=reservations

        List<ReservationFrontDTO> reservationFrontDTOs = this.reservationService.getReservationsFront();
        return new ResponseEntity(reservationFrontDTOs,HttpStatus.OK);
    }
}
