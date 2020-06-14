package com.team19.rentmicroservice.service.impl;

import com.team19.rentmicroservice.client.UserClient;
import com.team19.rentmicroservice.dto.MessageRequestDTO;
import com.team19.rentmicroservice.dto.MessageResponseDTO;
import com.team19.rentmicroservice.dto.UserInfoDTO;
import com.team19.rentmicroservice.model.Message;
import com.team19.rentmicroservice.model.Request;
import com.team19.rentmicroservice.model.UserInfo;
import com.team19.rentmicroservice.repository.MessageRepository;
import com.team19.rentmicroservice.security.CustomPrincipal;
import com.team19.rentmicroservice.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private RequestServiceImpl requestService;
    @Autowired
    private UserInfoServiceImpl userInfoService;
    @Autowired
    private UserClient userClient;

    @Override
    public List<MessageResponseDTO> findMessagesForRequest(Long id) {

        List<Message> messages = this.messageRepository.findMessagesForRequest(id);
        List<MessageResponseDTO> messageResponseDTOS = new ArrayList<>();
        for(Message m: messages){
            messageResponseDTOS.add(new MessageResponseDTO(m));
        }
        return messageResponseDTOS;
    }

    @Override
    public ResponseEntity<?> validateMessageRequest(MessageRequestDTO messageRequestDTO) {

        boolean valid = true;
        String msg = "";
        if(messageRequestDTO.getRequestId() <= 0){
            msg += "Request id has to be positive long number.";
            valid = false;
        }
        if(messageRequestDTO.getContent() == null || messageRequestDTO.getContent().equals("")){
            msg += "Message content is mandatory.";
            valid = false;
        }
        if(!valid){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
        Request request = this.requestService.findOne(messageRequestDTO.getRequestId());
        if(request == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Request with that id doesn't exist in the system.");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();
        //proverim da li je ulogovani korisnik vlasnik ili klijent u zahtevu
        if(Long.parseLong(cp.getUserID()) != request.getClientID() && Long.parseLong(cp.getUserID()) != request.getOwnerID()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to send messages for this request.");
        }

        return null;
    }

    @Override
    public MessageResponseDTO addMessage(MessageRequestDTO messageRequestDTO) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();

        UserInfo userInfo = userInfoService.findUserInfoByUserId(Long.parseLong(cp.getUserID()));
        //ako se korisnik ne nalazi ovde u bazi, dovucem ga iz user servisa i sacuvam ovde
        if(userInfo == null){
            UserInfoDTO userInfoDTO = this.userClient.getUserInfo(Long.parseLong(cp.getUserID()),cp.getToken());
            userInfo = new UserInfo(userInfoDTO);
            userInfo = this.userInfoService.saveUserInfo(userInfo);
        }

        Request request = this.requestService.findOne(messageRequestDTO.getRequestId());

        Message message = new Message(messageRequestDTO.getContent(), LocalDateTime.now(),userInfo,request);
        message = messageRepository.save(message);
        return new MessageResponseDTO(message);
    }
}
