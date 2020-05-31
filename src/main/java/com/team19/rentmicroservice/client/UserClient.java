package com.team19.rentmicroservice.client;

import com.team19.rentmicroservice.dto.CartItemDTO;
import com.team19.rentmicroservice.dto.ClientFrontDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserClient {

    @PostMapping("/client/fill")
    List<ClientFrontDTO> fillClients(@RequestBody List<ClientFrontDTO> clientFrontDTOs, @RequestHeader("Authorization") String token);
}
