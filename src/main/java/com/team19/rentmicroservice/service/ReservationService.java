package com.team19.rentmicroservice.service;

import com.team19.rentmicroservice.dto.ReservationDTO;
import com.team19.rentmicroservice.dto.ReservationFrontDTO;
import com.team19.rentmicroservice.model.Reservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface ReservationService {

    Reservation createNewReservation(ReservationDTO r, Long ownerID);
    Set<Reservation> findReservationsForThisAd(Long id);
    boolean checkIfAdReserved(Long adID, LocalDate startDate, LocalDate endDate);
    List<ReservationFrontDTO> getReservationsFront();
}
