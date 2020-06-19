package com.team19.rentmicroservice.service;

import com.rent_a_car.rent_service.soap.AddReservationRequest;
import com.rent_a_car.rent_service.soap.AddReservationResponse;
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
    AddReservationResponse addNewReservationFromAgentApp(AddReservationRequest arr);
    Reservation findById(Long id);
    Reservation save(Reservation reservation);
}
