package com.team19.rentmicroservice.controller;

import com.team19.rentmicroservice.dto.ReportDTO;
import com.team19.rentmicroservice.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/reports", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping(value = "/request",consumes = "application/json")
    @PreAuthorize("hasAuthority('report_create')")
    public ResponseEntity<?> createRequestReport(@RequestBody ReportDTO report){

        if(this.reportService.createRequestReport(report.getRequestAdId(), report.getContent(), report.getKm())){
            return new ResponseEntity("Report successfully created",HttpStatus.CREATED);
        }
        else return new ResponseEntity<>("Request not found", HttpStatus.BAD_REQUEST);

    }

    @PostMapping(value = "/reservation",consumes = "application/json")
    @PreAuthorize("hasAuthority('report_create')")
    public ResponseEntity<?> createReservationReport(@RequestBody ReportDTO report){

        if(this.reportService.createReservationReport(report.getReservationId(), report.getContent(), report.getKm()))
        {
            return new ResponseEntity("Report successfully created", HttpStatus.CREATED);
        }
        else return new ResponseEntity<>("Reservation not found", HttpStatus.BAD_REQUEST);

    }

    @GetMapping(value="/request/{reqAdId}")
    @PreAuthorize("hasAuthority('report_read')")
    public ResponseEntity<?> showRequestReport(@PathVariable("reqAdId") Long reqAdId){

        ReportDTO  report = this.reportService.showRequestReport(reqAdId);

        if( report != null)
        {
            return new ResponseEntity(report, HttpStatus.CREATED);
        }
        else return new ResponseEntity<>("Report not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping(value="/reservation/{resId}")
    @PreAuthorize("hasAuthority('report_read')")
    public ResponseEntity<?> showReservationReport(@PathVariable("resId") Long resId){

        ReportDTO  report = this.reportService.showReservationReport(resId);

        if( report != null)
        {
            return new ResponseEntity(report, HttpStatus.CREATED);
        }
        else return new ResponseEntity<>("Report not found", HttpStatus.NOT_FOUND);
    }

}
