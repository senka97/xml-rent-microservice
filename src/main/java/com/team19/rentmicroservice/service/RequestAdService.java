package com.team19.rentmicroservice.service;

import com.team19.rentmicroservice.model.RequestAd;

import java.time.LocalDate;

public interface RequestAdService {

    boolean checkIfAdReserved(Long adID, LocalDate startDate,LocalDate endDate);
    RequestAd findById(Long id);
    RequestAd save(RequestAd requestAd);
}
