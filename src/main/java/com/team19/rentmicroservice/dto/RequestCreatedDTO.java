package com.team19.rentmicroservice.dto;

import com.team19.rentmicroservice.model.Request;
import com.team19.rentmicroservice.model.RequestAd;

import java.util.ArrayList;
import java.util.List;

public class RequestCreatedDTO {

    private Long id;
    private String status;
    private List<RequestAdCreatedDTO> requestAdCreatedDTOS;
    private Long ownerID;
    private Long clientID;

    public RequestCreatedDTO(){

    }

    public RequestCreatedDTO(Request request){
        this.id = request.getId();
        this.status = request.getStatus().toString();
        this.ownerID = request.getOwnerID();
        this.clientID = request.getClientID();
        this.requestAdCreatedDTOS = new ArrayList<>();
        for(RequestAd ra:request.getRequestAds()){
            this.requestAdCreatedDTOS.add(new RequestAdCreatedDTO(ra));
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<RequestAdCreatedDTO> getRequestAdCreatedDTOS() {
        return requestAdCreatedDTOS;
    }

    public void setRequestAdCreatedDTOS(List<RequestAdCreatedDTO> requestAdCreatedDTOS) {
        this.requestAdCreatedDTOS = requestAdCreatedDTOS;
    }

    public Long getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(Long ownerID) {
        this.ownerID = ownerID;
    }

    public Long getClientID() {
        return clientID;
    }

    public void setClientID(Long clientID) {
        this.clientID = clientID;
    }
}
