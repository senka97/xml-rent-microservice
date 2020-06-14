package com.team19.rentmicroservice.service;

import com.team19.rentmicroservice.dto.MessageRequestDTO;
import com.team19.rentmicroservice.dto.MessageResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface MessageService {

    List<MessageResponseDTO> findMessagesForRequest(Long id);
    ResponseEntity<?> validateMessageRequest(MessageRequestDTO messageRequestDTO);
    MessageResponseDTO addMessage(MessageRequestDTO messageRequestDTO);
}
