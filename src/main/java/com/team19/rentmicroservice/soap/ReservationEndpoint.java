package com.team19.rentmicroservice.soap;

import com.rent_a_car.rent_service.soap.AddReservationRequest;
import com.rent_a_car.rent_service.soap.AddReservationResponse;
import com.team19.rentmicroservice.security.CustomPrincipal;
import com.team19.rentmicroservice.service.impl.ReservationServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class ReservationEndpoint {

    private static final String NAMESPACE_URI = "http://www.rent-a-car.com/rent-service/soap";

    @Autowired
    private ReservationServiceImpl reservationService;

    Logger logger = LoggerFactory.getLogger(ReservationServiceImpl.class);

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "addReservationRequest")
    @ResponsePayload
    public AddReservationResponse addReservation(@RequestPayload AddReservationRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();
        logger.info("SR-add reservation;UserID:" + cp.getUserID()); //SR=saop request

        AddReservationResponse addReservationResponse = this.reservationService.addNewReservationFromAgentApp(request);
        return addReservationResponse;
    }
}
