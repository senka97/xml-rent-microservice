package com.team19.rentmicroservice.service.impl;

import com.team19.rentmicroservice.dto.BillDTO;
import com.team19.rentmicroservice.model.Bill;
import com.team19.rentmicroservice.repository.BillRepository;
import com.team19.rentmicroservice.security.CustomPrincipal;
import com.team19.rentmicroservice.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class BillServiceImpl implements BillService {

    @Autowired
    private BillRepository billRepository;

    @Override
    public Bill save(Bill b) {
        return this.billRepository.save(b);
    }

    @Override
    public Boolean clientHasUnpaidBills(Long clientId) {

        ArrayList<Bill> unpaidBills = this.billRepository.findClientsBills(clientId, false);

        if(unpaidBills.size() == 0 )
        {
            System.out.println("Klijent nema neplacenih racuna");
            return false;
        }
        else return true;
    }

    @Override
    public Boolean payBill(Long billId, Long requestAdId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();
        Long clientId = Long.parseLong(cp.getUserID());

        Bill bill = this.billRepository.findBill(billId, requestAdId, clientId, false);

        if(bill != null)
        {
            bill.setPaid(true);
            this.billRepository.save(bill);
            System.out.println("Klijent platio racun");
            return true;
        }
        else return false;
    }

    @Override
    public int getBillsNumber() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();
        ArrayList<Bill> bills = this.billRepository.findClientsBills(Long.parseLong(cp.getUserID()), false);
        return bills.size();
    }

    @Override
    public ArrayList<BillDTO> getBills() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();
        ArrayList<Bill> bills = this.billRepository.findClientsBills(Long.parseLong(cp.getUserID()), false);

        ArrayList<BillDTO> billDTOs = new ArrayList<>();

        if(bills.size() == 0)
        {
            return billDTOs;
        }

        for( Bill b : bills)
        {
            BillDTO newBill = new BillDTO(b.getId(), b.getRequestAd().getId() ,b.getPayment(), b.getKmLimit(), b.getKmExceeded());

            billDTOs.add(newBill);
        }

        return billDTOs;
    }
}
