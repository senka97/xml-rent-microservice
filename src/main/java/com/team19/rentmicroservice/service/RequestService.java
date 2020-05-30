package com.team19.rentmicroservice.service;

import com.team19.rentmicroservice.dto.RentRequestDTO;
import com.team19.rentmicroservice.dto.RequestCreatedDTO;
import com.team19.rentmicroservice.model.Request;

import java.time.LocalDate;
import java.util.List;

public interface RequestService {

      List<RequestCreatedDTO> createRequests(RentRequestDTO rentRequestDTO);
      List<Request> findPendingRequests(Long adID, LocalDate startDate, LocalDate endDate);
      List<Request> saveAll(List<Request> requests);
}
