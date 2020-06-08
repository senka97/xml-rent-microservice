package com.team19.rentmicroservice.repository;

import com.team19.rentmicroservice.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request,Long> {

    @Query(value="FROM Request r inner join r.requestAds ra WHERE r.status='Pending' AND ra.adID=?1 AND ((ra.startDate<?2 AND ra.endDate>=?2) OR (ra.startDate>=?2 AND ra.endDate<=?3) OR (ra.startDate<=?3 AND ra.endDate>?3) OR (ra.startDate<?2 AND ra.endDate>?3))")
    List<Request> findPendingRequests(Long adID, LocalDate startDate, LocalDate endDate);

    @Query(value="FROM Request r WHERE r.status='Pending' AND r.ownerID=?1")
    List<Request> findAllPendingRequestsForOwner(Long ownerID);

    @Query(value="FROM Request r WHERE r.status='Pending' AND r.clientID=?1")
    List<Request> findAllPendingRequestsForClient(Long clientID);

    @Query(value="FROM Request r WHERE r.status='Pending' AND r.creationTime<?1")
    List<Request> findPendingRequestsAfter24(LocalDateTime time);

}
