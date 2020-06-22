package com.team19.rentmicroservice.soap;

import com.rent_a_car.rent_service.soap.*;
import com.team19.rentmicroservice.security.CustomPrincipal;
import com.team19.rentmicroservice.service.impl.RequestServiceImpl;
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
public class RequestEndpoint {

    private static final String NAMESPACE_URI = "http://www.rent-a-car.com/rent-service/soap";

    @Autowired
    private RequestServiceImpl requestService;

    Logger logger = LoggerFactory.getLogger(RequestEndpoint.class);

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getPendingRRequest")
    @ResponsePayload
    public GetPendingRResponse getPendingRequests(@RequestPayload GetPendingRRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();
        logger.info("SR-get pendingR;UserID:" + cp.getUserID()); //SR=saop request
        GetPendingRResponse response = this.requestService.findPendingRequestForAgentApp(request);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "rejectPendingRRequest")
    @ResponsePayload
    public RejectPendingRResponse rejectPendingRequests(@RequestPayload RejectPendingRRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();
        logger.info("SR-reject pendingR;UserID:" + cp.getUserID()); //SR=saop request
        RejectPendingRResponse response = this.requestService.rejectPendingRequestFromAgentApp(request);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "acceptPendingRRequest")
    @ResponsePayload
    public AcceptPendingRResponse acceptPendingRequests(@RequestPayload AcceptPendingRRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();
        logger.info("SR-accept pendingR;UserID:" + cp.getUserID()); //SR=saop request
        AcceptPendingRResponse response = this.requestService.acceptPendingRequestFromAgentApp(request);
        return response;
    }
}
