package com.team19.rentmicroservice.service;

import com.team19.rentmicroservice.dto.ReservationDTO;
import com.team19.rentmicroservice.model.CartItem;
import com.team19.rentmicroservice.model.Reservation;

import java.time.LocalDate;
import java.util.Set;

public interface ReservationService {

    Reservation createNewReservation(ReservationDTO r, Long ownerID);
    Set<Reservation> findReservationsForThisAd(Long id);
    boolean checkIfAdReserved(Long adID, LocalDate startDate, LocalDate endDate);
}
