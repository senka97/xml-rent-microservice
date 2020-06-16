package com.team19.rentmicroservice.soap;

import com.rent_a_car.rent_service.soap.*;
import com.team19.rentmicroservice.service.impl.RequestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class RequestEndpoint {

    private static final String NAMESPACE_URI = "http://www.rent-a-car.com/rent-service/soap";

    @Autowired
    private RequestServiceImpl requestService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getPendingRRequest")
    @ResponsePayload
    public GetPendingRResponse getPendingRequests(@RequestPayload GetPendingRRequest request) {
        System.out.println("Uslo u pending requests");
        GetPendingRResponse response = this.requestService.findPendingRequestForAgentApp(request);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "rejectPendingRRequest")
    @ResponsePayload
    public RejectPendingRResponse rejectPendingRequests(@RequestPayload RejectPendingRRequest request) {
        System.out.println("Uslo u rejecting requests");
        RejectPendingRResponse response = this.requestService.rejectPendingRequestFromAgentApp(request);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "acceptPendingRRequest")
    @ResponsePayload
    public AcceptPendingRResponse acceptPendingRequests(@RequestPayload AcceptPendingRRequest request) {
        System.out.println("Uslo u accepting requests");
        AcceptPendingRResponse response = this.requestService.acceptPendingRequestFromAgentApp(request);
        return response;
    }
}
