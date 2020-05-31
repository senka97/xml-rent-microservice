package com.team19.rentmicroservice.dto;

import com.team19.rentmicroservice.model.Request;
import com.team19.rentmicroservice.model.RequestAd;

import java.util.ArrayList;
import java.util.List;

public class RequestFrontDTO {

    private Long id;
    private String status;
    private Long clientID;
    private String clientName;
    private String clientLastName;
    private Long ownerID;
    private List<RequestAdFrontDTO> requestAds;

    public RequestFrontDTO(){

    }

    public RequestFrontDTO(Request request){

        this.id = request.getId();
        this.status = request.getStatus().toString();
        this.clientID = request.getClientID();
        this.ownerID = request.getOwnerID();
        this.requestAds = new ArrayList<>();
        for(RequestAd ra: request.getRequestAds()){
            this.requestAds.add(new RequestAdFrontDTO(ra));
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

    public Long getClientID() {
        return clientID;
    }

    public void setClientID(Long clientID) {
        this.clientID = clientID;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientLastName() {
        return clientLastName;
    }

    public void setClientLastName(String clientLastName) {
        this.clientLastName = clientLastName;
    }

    public Long getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(Long ownerID) {
        this.ownerID = ownerID;
    }

    public List<RequestAdFrontDTO> getRequestAds() {
        return requestAds;
    }

    public void setRequestAds(List<RequestAdFrontDTO> requestAds) {
        this.requestAds = requestAds;
    }
}
