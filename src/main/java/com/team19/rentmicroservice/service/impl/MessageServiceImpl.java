package com.team19.rentmicroservice.service.impl;

import com.rent_a_car.rent_service.soap.*;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.MessageFormat;
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

    Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();

        boolean valid = true;
        String msg = "";
        String logMsg = "";
        if(messageRequestDTO.getRequestId() <= 0){
            msg += "Request id has to be positive long number.";
            valid = false;
            logMsg += "RID-inv "; //Request id invalid
        }
        if(messageRequestDTO.getContent() == null || messageRequestDTO.getContent().equals("")){
            msg += "Message content is mandatory.";
            valid = false;
            logMsg += "MC-miss "; //messages content missing
        }
        if(!valid){
            logger.warn(MessageFormat.format("MsgR-invalid:{0};UserID:{1}",logMsg, cp.getUserID())); //msgR=messageRequest
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
        Request request = this.requestService.findOne(messageRequestDTO.getRequestId());
        if(request == null){
            logger.warn(MessageFormat.format("MsgR-invalid:R-ID {0} not found;UserID:{1}", messageRequestDTO.getRequestId(), cp.getUserID())); //msgR=messageRequest, R-ID=request id
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Request with that id doesn't exist in the system.");
        }


        //proverim da li je ulogovani korisnik vlasnik ili klijent u zahtevu
        if(Long.parseLong(cp.getUserID()) != request.getClientID() && Long.parseLong(cp.getUserID()) != request.getOwnerID()){
            logger.warn(MessageFormat.format("MsgR-invalid:R-ID {0} not from user;UserID:{1}", messageRequestDTO.getRequestId(), cp.getUserID())); //msgR=messageRequest, R-ID=request id
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
            logger.debug("US-call-S:RUI:" + cp.getUserID()); //User service call start, RUI - retrieving user info
            UserInfoDTO userInfoDTO = this.userClient.getUserInfo(Long.parseLong(cp.getUserID()),cp.getToken());
            logger.debug("US-call-E:RUI:" + cp.getUserID()); //User service call end, RUI - retrieving user info
            userInfo = new UserInfo(userInfoDTO);
            userInfo = this.userInfoService.saveUserInfo(userInfo);
        }

        Request request = this.requestService.findOne(messageRequestDTO.getRequestId());

        Message message = new Message(messageRequestDTO.getContent(), LocalDateTime.now(),userInfo,request);
        message = messageRepository.save(message);
        return new MessageResponseDTO(message);
    }

    @Override
    @Transactional //zbog userInfo, sesija se zatvori
    public GetMessagesResponse getMessagesForAgentApp(GetMessagesRequest gmr) {

        List<Message> newMessages;
        //ako su postojale neke poruke na agentu uzmi nove, koje nisu te
        if(gmr.getExistingMessages().size()>0) {
            newMessages = this.messageRepository.findNewMessagesForRequestForAgentApp(gmr.getMainIdRequest(), gmr.getExistingMessages());
        }else{ //ako nisu postojale poruke na agentu uzmi sve za taj zahtev
            newMessages = this.messageRepository.findMessagesForRequest(gmr.getMainIdRequest());
        }

        List<MessageResponseSOAP> messageResponseSOAPS = new ArrayList<>();
        for(Message m: newMessages){
            MessageResponseSOAP mrs = new MessageResponseSOAP();
            mrs.setMainId(m.getId());
            mrs.setContent(m.getContent());
            mrs.setDateTime(m.getDateTime().toString());
            mrs.setFromUserId(m.getFromUserInfo().getUserId());
            if(m.getFromUserInfo().getRole().equals("ROLE_AGENT")){
                mrs.setFromUserInfo(m.getFromUserInfo().getName() + " " + m.getFromUserInfo().getSurname() + " (" + m.getFromUserInfo().getCompanyName() + ")");
            }else{
                mrs.setFromUserInfo(m.getFromUserInfo().getName() + " " + m.getFromUserInfo().getSurname());
            }
            messageResponseSOAPS.add(mrs);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();

        GetMessagesResponse gmResponse = new GetMessagesResponse();
        gmResponse.setAgentId(Long.parseLong(cp.getUserID()));
        gmResponse.getMessageResponseSOAP().addAll(messageResponseSOAPS);

        return gmResponse;
    }

    @Override
    @Transactional //zbog userInfo, sesija se zatvori
    public AddMessageResponse addMessageFromAgentApp(AddMessageRequest amr) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();

        UserInfo userInfo = userInfoService.findUserInfoByUserId(Long.parseLong(cp.getUserID()));
        //ako se korisnik ne nalazi ovde u bazi, dovucem ga iz user servisa i sacuvam ovde
        if(userInfo == null){
            logger.debug("US-call-S:RUI:" + cp.getUserID()); //User service call start, RUI - retrieving user info
            UserInfoDTO userInfoDTO = this.userClient.getUserInfo(Long.parseLong(cp.getUserID()),cp.getToken());
            logger.debug("US-call-E:RUI:" + cp.getUserID()); //User service call end, RUI - retrieving user info
            userInfo = new UserInfo(userInfoDTO);
            userInfo = this.userInfoService.saveUserInfo(userInfo);
        }

        Request request = this.requestService.findOne(amr.getMainIdRequest());

        //ako zahtev ne postoji vraca se kao neuspeh
        if(request == null){
            logger.warn(MessageFormat.format("R-ID:{0}-from agent NF;UserID:{1}", amr.getMainIdRequest(), cp.getUserID())); //R-ID=request id, NF-not found
            AddMessageResponse amResponse = new AddMessageResponse();
            amResponse.setSuccess(false);
            MessageResponseSOAP mrs = new MessageResponseSOAP();
            amResponse.setMessageResponseSOAP(mrs);
            return amResponse;
        }

        Message message = new Message(amr.getContent(), LocalDateTime.now(),userInfo,request);
        message = messageRepository.save(message);
        logger.info(MessageFormat.format("Msg-ID:{0}-from agent added;UserID:{1}", message.getId()), cp.getUserID()); //msg-ID=message id

        MessageResponseSOAP mrs = new MessageResponseSOAP();
        mrs.setMainId(message.getId());
        mrs.setContent(message.getContent());
        mrs.setDateTime(message.getDateTime().toString());
        mrs.setFromUserId(message.getFromUserInfo().getUserId());
        if(message.getFromUserInfo().getRole().equals("ROLE_AGENT")){
            mrs.setFromUserInfo(message.getFromUserInfo().getName() + " " + message.getFromUserInfo().getSurname() + " (" + message.getFromUserInfo().getCompanyName() + ")");
        }else{
            mrs.setFromUserInfo(message.getFromUserInfo().getName() + " " + message.getFromUserInfo().getSurname());
        }

        AddMessageResponse amResponse = new AddMessageResponse();
        amResponse.setSuccess(true);
        amResponse.setMessageResponseSOAP(mrs);
        return amResponse;
    }


}
