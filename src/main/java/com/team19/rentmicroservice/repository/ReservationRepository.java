package com.team19.rentmicroservice.repository;

import com.team19.rentmicroservice.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query(value = "SELECT * FROM reservation r WHERE r.adid= ?1 "  , nativeQuery = true)
    Set<Reservation> findReservationsForThisAd(Long id);

    @Query(value="FROM Reservation r WHERE r.adID=?1 AND ((r.startDate<?2 AND r.endDate>=?2) OR (r.startDate>=?2 AND r.endDate<=?3) OR (r.startDate<=?3 AND r.endDate>?3) OR (r.startDate<?2 AND r.endDate>?3))")
    List<Reservation> findReservations(Long adID, LocalDate startDateCI, LocalDate endDateCI);

    @Query(value="FROM Reservation r WHERE r.ownerID=?1")
    List<Reservation> findReservationsForThisOwner(Long ownerID);
}
