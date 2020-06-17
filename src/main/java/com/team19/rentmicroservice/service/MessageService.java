package com.team19.rentmicroservice.service;

import com.rent_a_car.rent_service.soap.AddMessageRequest;
import com.rent_a_car.rent_service.soap.AddMessageResponse;
import com.rent_a_car.rent_service.soap.GetMessagesRequest;
import com.rent_a_car.rent_service.soap.GetMessagesResponse;
import com.team19.rentmicroservice.dto.MessageRequestDTO;
import com.team19.rentmicroservice.dto.MessageResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface MessageService {

    List<MessageResponseDTO> findMessagesForRequest(Long id);
    ResponseEntity<?> validateMessageRequest(MessageRequestDTO messageRequestDTO);
    MessageResponseDTO addMessage(MessageRequestDTO messageRequestDTO);
    GetMessagesResponse getMessagesForAgentApp(GetMessagesRequest gmr);
    AddMessageResponse addMessageFromAgentApp(AddMessageRequest amr);
}
