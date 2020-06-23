package com.team19.rentmicroservice.controller;

import com.team19.rentmicroservice.dto.MessageRequestDTO;
import com.team19.rentmicroservice.dto.MessageResponseDTO;
import com.team19.rentmicroservice.model.Request;
import com.team19.rentmicroservice.security.CustomPrincipal;
import com.team19.rentmicroservice.service.impl.MessageServiceImpl;
import com.team19.rentmicroservice.service.impl.RequestServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.List;

@RestController
@RequestMapping(value = "/api/message", produces = MediaType.APPLICATION_JSON_VALUE)
public class MessageController {

    @Autowired
    private MessageServiceImpl messageService;
    @Autowired
    private RequestServiceImpl requestService;

    Logger logger = LoggerFactory.getLogger(MessageController.class);

    @GetMapping(value = "/request/{id}", produces = "application/json")
    @PreAuthorize("hasAuthority('message_r_c')")
    public ResponseEntity<?> getMessagesForRequest(@PathVariable("id") Long id){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();

        if(id <= 0){
            logger.warn(MessageFormat.format("R-ID:{0}-invalid;UserID:{1}", id, cp.getUserID()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request id has to be positive long number.");
        }
        Request request = this.requestService.findOne(id);
        if(request == null){
            logger.warn(MessageFormat.format("R-ID:{0}-not found;UserID:{1}", id, cp.getUserID()));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Request with that id doesn't exist in the system.");
        }
        List<MessageResponseDTO> messageResponseDTOS = this.messageService.findMessagesForRequest(id);
        logger.info(MessageFormat.format("R-ID:{0}-msgs read;UserID:{1}", id, cp.getUserID()));
        return new ResponseEntity(messageResponseDTOS, HttpStatus.OK);
    }

    @PostMapping(produces = "application/json", consumes = "application/json")
    @PreAuthorize("hasAuthority('message_r_c')")
    public ResponseEntity<?> addMessage(@RequestBody MessageRequestDTO messageRequestDTO){

           Authentication auth = SecurityContextHolder.getContext().getAuthentication();
           CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();

           ResponseEntity<?> responseEntity = this.messageService.validateMessageRequest(messageRequestDTO);
           if(responseEntity != null){
               return responseEntity;
           }

        MessageResponseDTO msgResponse = this.messageService.addMessage(messageRequestDTO);
        logger.info(MessageFormat.format("Msg-ID:{0}-added;UserID:{1}", msgResponse.getId(), cp.getUserID()));
        return new ResponseEntity(msgResponse, HttpStatus.CREATED);
    }


}
