package com.team19.rentmicroservice.soap;

import com.rent_a_car.rent_service.soap.GetTestRequest;
import com.rent_a_car.rent_service.soap.GetTestResponse;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class TestEndpoint {

    private static final String NAMESPACE_URI = "http://www.rent-a-car.com/rent-service/soap";

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getTestRequest")
    @ResponsePayload
    public GetTestResponse getTest(@RequestPayload GetTestRequest request) {
        System.out.println("Uslo u test");
        GetTestResponse response = new GetTestResponse();
        response.setResponse("Radi soap: " + request.getName());

        return response;
    }

}
