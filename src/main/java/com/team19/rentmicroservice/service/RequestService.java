package com.team19.rentmicroservice.service;

import com.team19.rentmicroservice.dto.RentRequestDTO;
import com.team19.rentmicroservice.dto.RequestCreatedDTO;

import java.util.List;

public interface RequestService {

      List<RequestCreatedDTO> createRequests(RentRequestDTO rentRequestDTO);
}
