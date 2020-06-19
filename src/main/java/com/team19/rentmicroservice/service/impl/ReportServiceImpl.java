package com.team19.rentmicroservice.service.impl;

import com.team19.rentmicroservice.client.AdClient;
import com.team19.rentmicroservice.dto.ReportDTO;
import com.team19.rentmicroservice.model.Report;
import com.team19.rentmicroservice.model.RequestAd;
import com.team19.rentmicroservice.model.Reservation;
import com.team19.rentmicroservice.repository.ReportRepository;
import com.team19.rentmicroservice.security.CustomPrincipal;
import com.team19.rentmicroservice.service.ReportService;
import com.team19.rentmicroservice.service.RequestAdService;
import com.team19.rentmicroservice.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private RequestAdService requestAdService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private AdClient adClient;

    @Override
    @Transactional
    public Boolean createRequestReport(Long requestAdId, String content, double km) {

        RequestAd requestAd = requestAdService.findById(requestAdId);

        if(requestAd != null && !requestAd.getReportCreated())
        {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();

            if (this.adClient.changeMileageAfterReport(requestAd.getAdID(), km, cp.getPermissions(), cp.getUserID(), cp.getToken()))
            {

                Report r = new Report();
                r.setContent(content);
                r.setKm(km);
                r.setRequestAd(requestAd);
                r.setReservation(null);
                requestAd.setReportCreated(true);

                requestAdService.save(requestAd);
                reportRepository.save(r);
                return true;

            }
            else return false;
        }
        else return false;
    }

    @Override
    @Transactional
    public Boolean createReservationReport(Long reservationId, String content, double km) {

        Reservation reservation = reservationService.findById(reservationId);

        if(reservation != null && !reservation.getReportCreated())
        {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();

            if(this.adClient.changeMileageAfterReport(reservation.getAdID(), km, cp.getPermissions(), cp.getUserID(), cp.getToken()))
            {
                Report r = new Report();
                r.setContent(content);
                r.setKm(km);
                r.setRequestAd(null);
                r.setReservation(reservation);
                reservation.setReportCreated(true);

                reservationService.save(reservation);
                reportRepository.save(r);
                return true;
            }
            else return false;
        }
        else return false;
    }

    @Override
    public ReportDTO showRequestReport(Long id) {

        Report report = this.reportRepository.findByRequestAdId(id);

        if(report != null)
        {
            ReportDTO r = new ReportDTO();
            r.setId(report.getId());
            r.setKm(report.getKm());
            r.setContent(report.getContent());

            return r;
        }
        else return null;
    }

    @Override
    public ReportDTO showReservationReport(Long id) {

        Report report = this.reportRepository.findByReservationId(id);

        if(report != null)
        {
            ReportDTO r = new ReportDTO();
            r.setId(report.getId());
            r.setKm(report.getKm());
            r.setContent(report.getContent());

            return r;
        }
        else return null;

    }
}
