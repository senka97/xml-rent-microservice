package com.team19.rentmicroservice.repository;


import com.team19.rentmicroservice.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;

public interface BillRepository extends JpaRepository<Bill, Long> {

    @Query(value="FROM Bill b WHERE b.clientId = ?1 AND b.paid = ?2")
    ArrayList<Bill> findClientsBills(Long clientId, Boolean status);

    @Query(value="FROM Bill b WHERE b.id = ?1 AND b.requestAd.id = ?2 AND b.clientId = ?3 AND b.paid = ?4")
    Bill findBill(Long billId, Long requestAdId, Long clientId, Boolean status);

}


