package com.team19.rentmicroservice.service.impl;

import com.team19.rentmicroservice.model.CartItem;
import com.team19.rentmicroservice.model.Request;
import com.team19.rentmicroservice.model.RequestAd;
import com.team19.rentmicroservice.repository.RequestAdRepository;
import com.team19.rentmicroservice.service.RequestAdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RequestAdServiceImpl implements RequestAdService {

    @Autowired
    private RequestAdRepository requestAdRepository;

    @Override
    public boolean checkIfAdReserved(Long adID, LocalDate startDate, LocalDate endDate) {

        List<RequestAd> requestAds = this.requestAdRepository.findRequests(adID,startDate,endDate);
        if(requestAds.size() == 0){
            return false;
        }else{
            return true;
        }
    }
}
