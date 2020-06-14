package com.team19.rentmicroservice.dto;

import com.team19.rentmicroservice.model.Message;

import java.time.LocalDateTime;

public class MessageResponseDTO {

    private Long id;
    private String content;
    private Long fromUserId;
    private String fromUserInfo;
    private LocalDateTime dateTime;

    public MessageResponseDTO(){

    }

    public MessageResponseDTO(Message message){
        this.id = message.getId();
        this.content = message.getContent();
        this.fromUserId = message.getFromUserInfo().getUserId();
        this.dateTime = message.getDateTime();
        if(message.getFromUserInfo().getRole().equals("ROLE_AGENT")){
            this.fromUserInfo = message.getFromUserInfo().getName() + " " + message.getFromUserInfo().getSurname() + " (" + message.getFromUserInfo().getCompanyName() + ")";
        }else{
            this.fromUserInfo = message.getFromUserInfo().getName() + " " + message.getFromUserInfo().getSurname();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getFromUserInfo() {
        return fromUserInfo;
    }

    public void setFromUserInfo(String fromUserInfo) {
        this.fromUserInfo = fromUserInfo;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
