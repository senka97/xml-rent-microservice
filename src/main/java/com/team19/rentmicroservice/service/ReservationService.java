package com.team19.rentmicroservice.service;

import com.team19.rentmicroservice.dto.ReservationDTO;
import com.team19.rentmicroservice.model.Reservation;

import java.util.Set;

public interface ReservationService {

    Reservation createNewReservation(ReservationDTO r);
    Set<Reservation> findReservationsForThisAd(Long id);
}
