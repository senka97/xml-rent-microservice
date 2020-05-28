package com.team19.rentmicroservice.repository;

import com.team19.rentmicroservice.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query(value = "SELECT * FROM reservation r WHERE r.adid= ?1 "  , nativeQuery = true)
    Set<Reservation> findReservationsForThisAd(Long id);
}
