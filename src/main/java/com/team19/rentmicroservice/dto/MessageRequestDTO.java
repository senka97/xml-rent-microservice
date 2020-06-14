package com.team19.rentmicroservice.dto;

public class MessageRequestDTO {

    private Long requestId;
    private String content;

    public MessageRequestDTO(){

    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
