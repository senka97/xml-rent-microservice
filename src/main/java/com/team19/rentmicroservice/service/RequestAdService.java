package com.team19.rentmicroservice.service;

import com.team19.rentmicroservice.model.CartItem;

import java.time.LocalDate;

public interface RequestAdService {

    boolean checkIfAdReserved(Long adID, LocalDate startDate,LocalDate endDate);
}
