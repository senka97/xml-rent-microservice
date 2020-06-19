package com.team19.rentmicroservice.service;

import com.team19.rentmicroservice.dto.ReportDTO;

public interface ReportService {

    Boolean createRequestReport(Long requestAdId, String content, double km);
    Boolean createReservationReport(Long reservation, String content, double km);
    ReportDTO showRequestReport(Long id);
    ReportDTO showReservationReport(Long id);

}
