package com.team19.rentmicroservice.controller;

import com.team19.rentmicroservice.dto.MessageRequestDTO;
import com.team19.rentmicroservice.dto.MessageResponseDTO;
import com.team19.rentmicroservice.model.Request;
import com.team19.rentmicroservice.service.impl.MessageServiceImpl;
import com.team19.rentmicroservice.service.impl.RequestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/message", produces = MediaType.APPLICATION_JSON_VALUE)
public class MessageController {

    @Autowired
    private MessageServiceImpl messageService;
    @Autowired
    private RequestServiceImpl requestService;

    @GetMapping(value = "/request/{id}", produces = "application/json")
    @PreAuthorize("hasAuthority('message_r_c')")
    public ResponseEntity<?> getMessagesForRequest(@PathVariable("id") Long id){

        if(id <= 0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request id has to be positive long number.");
        }
        Request request = this.requestService.findOne(id);
        if(request == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Request with that id doesn't exist in the system.");
        }
        List<MessageResponseDTO> messageResponseDTOS = this.messageService.findMessagesForRequest(id);
        return new ResponseEntity(messageResponseDTOS, HttpStatus.OK);
    }

    @PostMapping(produces = "application/json", consumes = "application/json")
    @PreAuthorize("hasAuthority('message_r_c')")
    public ResponseEntity<?> addMessage(@RequestBody MessageRequestDTO messageRequestDTO){

           ResponseEntity<?> responseEntity = this.messageService.validateMessageRequest(messageRequestDTO);
           if(responseEntity != null){
               return responseEntity;
           }

           return new ResponseEntity(this.messageService.addMessage(messageRequestDTO), HttpStatus.OK);
    }


}
