package com.team19.rentmicroservice.soap;

import com.rent_a_car.rent_service.soap.AddReservationRequest;
import com.rent_a_car.rent_service.soap.AddReservationResponse;
import com.team19.rentmicroservice.service.impl.ReservationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class ReservationEndpoint {

    private static final String NAMESPACE_URI = "http://www.rent-a-car.com/rent-service/soap";

    @Autowired
    private ReservationServiceImpl reservationService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "addReservationRequest")
    @ResponsePayload
    public AddReservationResponse addReservation(@RequestPayload AddReservationRequest request) {
        System.out.println("Uslo u dodavanje rezervacije");
        AddReservationResponse addReservationResponse = this.reservationService.addNewReservationFromAgentApp(request);
        return addReservationResponse;
    }
}
