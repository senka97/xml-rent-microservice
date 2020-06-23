package com.team19.rentmicroservice.controller;

import com.team19.rentmicroservice.dto.ReportDTO;
import com.team19.rentmicroservice.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/reports", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReportController {

    @Autowired
    private ReportService reportService;

    Logger logger = LoggerFactory.getLogger(ReportController.class);

    @PostMapping(value = "/request",consumes = "application/json")
    @PreAuthorize("hasAuthority('report_create')")
    public ResponseEntity<?> createRequestReport(@Valid @RequestBody ReportDTO report){

        if(report.getRequestAdId() != null)
        {
            if(report.getRequestAdId() < 0)
            {
                logger.error("BR - RequestAd id can't be negative number");
                return new ResponseEntity<>("Id can't be negative number", HttpStatus.BAD_REQUEST);
            }

        }
        else
        {
            logger.error("BR - RequestAd id can't be null");
            return new ResponseEntity<>("Id can't be null", HttpStatus.BAD_REQUEST);
        }


        if(this.reportService.createRequestReport(report.getRequestAdId(), report.getContent(), report.getKm())){
            logger.info("Creating report - Report for request id: " + report.getRequestAdId() + " created");
            return new ResponseEntity("Report successfully created", HttpStatus.CREATED);
        }
        else
        {
            logger.info("Creating report - Report for request id: " + report.getRequestAdId() + " couldn't be created");
            return new ResponseEntity<>("Error creating report", HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping(value = "/reservation",consumes = "application/json")
    @PreAuthorize("hasAuthority('report_create')")
    public ResponseEntity<?> createReservationReport(@Valid @RequestBody ReportDTO report){

        if(report.getReservationId() != null)
        {
            if(report.getReservationId() < 0)
            {
                logger.error("BR - Reservation id can't be negative number");
                return new ResponseEntity<>("Id can't be negative number", HttpStatus.BAD_REQUEST);
            }

        }
        else
        {
            logger.error("BR - Reservation id can't be null");
            return new ResponseEntity<>("Id can't be null", HttpStatus.BAD_REQUEST);
        }


        if(this.reportService.createReservationReport(report.getReservationId(), report.getContent(), report.getKm()))
        {
            logger.info("Creating report - Report for reservation id: " + report.getReservationId() + " created");
            return new ResponseEntity("Report successfully created", HttpStatus.CREATED);
        }
        else
        {
            logger.info("Creating report - Report for reservation id: " + report.getReservationId() + " couldn't be created");
            return new ResponseEntity<>("Error creating report", HttpStatus.BAD_REQUEST);
        }

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
