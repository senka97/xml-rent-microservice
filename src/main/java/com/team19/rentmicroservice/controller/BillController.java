package com.team19.rentmicroservice.controller;

import com.team19.rentmicroservice.dto.BillDTO;
import com.team19.rentmicroservice.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class BillController {

    @Autowired
    private BillService billService;

    @GetMapping(value = "/bills/{clientId}")
    public ResponseEntity<?> getClientHasUnpaidBills(@PathVariable("clientId") Long clientId)
    {
        Boolean flag = this.billService.clientHasUnpaidBills(clientId);
        return new ResponseEntity<>(flag, HttpStatus.OK);
    }

    @PutMapping(value = "/bills/{id}/{requestAdId}")
    public ResponseEntity<?> payBill(@PathVariable("id") Long billId, @PathVariable("requestAdId") Long requestAdId)
    {
        Boolean flag = this.billService.payBill(billId,requestAdId);
        return new ResponseEntity<>(flag, HttpStatus.OK);
    }

    @GetMapping(value="/bills/number")
    @PreAuthorize("hasAuthority('request_read')")
    public ResponseEntity<?> getBillsNumber(){

        int num = this.billService.getBillsNumber();
        return new ResponseEntity(num,HttpStatus.OK);
    }

    @GetMapping(value = "/bills")
    public ResponseEntity<?> getClientsUnpaidBills()
    {
        ArrayList<BillDTO> bills = this.billService.getBills();
        return new ResponseEntity<>(bills, HttpStatus.OK);
    }

}
