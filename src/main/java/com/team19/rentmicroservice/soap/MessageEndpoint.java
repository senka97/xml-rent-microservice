package com.team19.rentmicroservice.soap;

import com.rent_a_car.rent_service.soap.AddMessageRequest;
import com.rent_a_car.rent_service.soap.AddMessageResponse;
import com.rent_a_car.rent_service.soap.GetMessagesRequest;
import com.rent_a_car.rent_service.soap.GetMessagesResponse;
import com.team19.rentmicroservice.service.impl.MessageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class MessageEndpoint {

    private static final String NAMESPACE_URI = "http://www.rent-a-car.com/rent-service/soap";

    @Autowired
    private MessageServiceImpl messageService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getMessagesRequest")
    @ResponsePayload
    public GetMessagesResponse getMessages(@RequestPayload GetMessagesRequest request) {
        System.out.println("Uslo u get messages.");
        GetMessagesResponse response = this.messageService.getMessagesForAgentApp(request);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "addMessageRequest")
    @ResponsePayload
    public AddMessageResponse addMessage(@RequestPayload AddMessageRequest request) {
        System.out.println("Uslo u add message.");
        AddMessageResponse response = this.messageService.addMessageFromAgentApp(request);
        return response;
    }
}
