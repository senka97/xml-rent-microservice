package com.team19.rentmicroservice.repository;

import com.team19.rentmicroservice.model.RequestAd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RequestAdRepository extends JpaRepository<RequestAd, Long> {

    @Query(value="FROM RequestAd ra inner join ra.request r WHERE r.status='Paid' AND ra.adID=?1 AND ((ra.startDate<?2 AND ra.endDate>?2) OR (ra.startDate>=?2 AND ra.endDate<=?3) OR (ra.startDate<?3 AND ra.endDate>?3))")
    List<RequestAd> findRequests(Long adID, LocalDate startDateCI, LocalDate endDateCI);
}
