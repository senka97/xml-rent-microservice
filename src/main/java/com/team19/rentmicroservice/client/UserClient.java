package com.team19.rentmicroservice.client;

import com.team19.rentmicroservice.dto.CartItemDTO;
import com.team19.rentmicroservice.dto.ClientFrontDTO;
import com.team19.rentmicroservice.dto.UserInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserClient {

    @PostMapping("/client/fill")
    List<ClientFrontDTO> fillClients(@RequestBody List<ClientFrontDTO> clientFrontDTOs, @RequestHeader("Authorization") String token);
    @GetMapping("/user/info/{id}")
    UserInfoDTO getUserInfo(@PathVariable("id") Long id, @RequestHeader("Authorization") String token);
}
