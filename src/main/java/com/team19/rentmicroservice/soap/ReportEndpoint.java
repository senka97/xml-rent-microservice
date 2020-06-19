package com.team19.rentmicroservice.soap;

import com.rent_a_car.rent_service.soap.CreateRequestReportRequest;
import com.rent_a_car.rent_service.soap.CreateRequestReportResponse;
import com.rent_a_car.rent_service.soap.CreateReservationReportRequest;
import com.rent_a_car.rent_service.soap.CreateReservationReportResponse;
import com.team19.rentmicroservice.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class ReportEndpoint {

    private static final String NAMESPACE_URI = "http://www.rent-a-car.com/rent-service/soap";

    @Autowired
    private ReportService reportService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createRequestReportRequest")
    @ResponsePayload
    public CreateRequestReportResponse createRequestReport(@RequestPayload CreateRequestReportRequest request) {
        System.out.println("Uslo u create request report.");
        CreateRequestReportResponse response = new CreateRequestReportResponse();
        response.setReportCreated( this.reportService.createRequestReport(request.getReportSOAP().getRequestAdId(),
                 request.getReportSOAP().getContent(),request.getReportSOAP().getKm()));
        System.out.println("***********************************");
        System.out.println("Zavrsio kreiranje request reporta.");
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createReservationReportRequest")
    @ResponsePayload
    public CreateReservationReportResponse createReservationReport(@RequestPayload CreateReservationReportRequest request) {
        System.out.println("Uslo u create reservation report.");
        CreateReservationReportResponse response = new CreateReservationReportResponse();
        response.setReportCreated( this.reportService.createReservationReport(request.getReportSOAP().getReservationId(),
                request.getReportSOAP().getContent(),request.getReportSOAP().getKm()));

        System.out.println("***********************************");
        System.out.println("Zavrsio kreiranje reservation reporta.");
        return response;
    }

}
