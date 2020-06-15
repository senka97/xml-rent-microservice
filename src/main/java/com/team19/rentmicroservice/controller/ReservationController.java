package com.team19.rentmicroservice.controller;

import com.team19.rentmicroservice.client.AdClient;
import com.team19.rentmicroservice.dto.AdOwnerDTO;
import com.team19.rentmicroservice.dto.RequestFrontDTO;
import com.team19.rentmicroservice.dto.ReservationDTO;
import com.team19.rentmicroservice.dto.ReservationFrontDTO;
import com.team19.rentmicroservice.model.Reservation;
import com.team19.rentmicroservice.security.CustomPrincipal;
import com.team19.rentmicroservice.service.impl.ReservationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
@CrossOrigin
public class ReservationController {

    @Autowired
    private ReservationServiceImpl reservationService;
    @Autowired
    private AdClient adClient;

    @PostMapping(value="/reservations",consumes="application/json")
    @PreAuthorize("hasAuthority('reservation_create')")
    public ResponseEntity<?> createNewReservation(@RequestBody ReservationDTO reservationDTO)  {

        if(reservationDTO.getClientFirstName() == null || reservationDTO.getClientLastName() == null || reservationDTO.getClientEmail() == null || reservationDTO.getClientPhoneNumber() == null){
            return new ResponseEntity<>("All information about client must be entered!", HttpStatus.BAD_REQUEST);
        }

        if(reservationDTO.getStartDate() == null || reservationDTO.getEndDate() == null){
            return new ResponseEntity<>("Both start and end date must be selected",HttpStatus.BAD_REQUEST);
        }

        if(reservationDTO.getEndDate().isBefore(reservationDTO.getStartDate())){
            return new ResponseEntity<>("Start date must be before end date!",HttpStatus.BAD_REQUEST);
        }

        //dobavi se id vlasnika oglasa i proveri se da li je to oglas od trenutno ulogovanog
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();
        AdOwnerDTO adOwnerDTO = adClient.getAdOwner(reservationDTO.getAdId(), cp.getPermissions(),cp.getUserID(),cp.getToken());
        if(adOwnerDTO == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This ad doesn't exist.");
        }else{
            if(adOwnerDTO.getOwnerID() != Long.parseLong(cp.getUserID())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You can't make a reservation for an ad that is not yours.");
            }
        }

        Reservation r = reservationService.createNewReservation(reservationDTO, adOwnerDTO.getOwnerID());

        if(r == null)
        {
            return new ResponseEntity<>("These dates are already reserved",HttpStatus.EXPECTATION_FAILED);
        }
        else return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value="/reservations")
    @PreAuthorize("hasAuthority('reservation_read')")
    public ResponseEntity<?> getReservationsFront(){

        List<ReservationFrontDTO> reservationFrontDTOs = this.reservationService.getReservationsFront();
        return new ResponseEntity(reservationFrontDTOs,HttpStatus.OK);
    }
}
