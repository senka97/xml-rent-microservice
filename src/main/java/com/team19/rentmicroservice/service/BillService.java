package com.team19.rentmicroservice.service;

import com.team19.rentmicroservice.dto.BillDTO;
import com.team19.rentmicroservice.model.Bill;

import java.util.ArrayList;

public interface BillService {

    Bill save(Bill b);
    Boolean clientHasUnpaidBills(Long clientId);
    Boolean payBill(Long billId, Long requestAdId);
    int getBillsNumber();
    ArrayList<BillDTO> getBills();
}
