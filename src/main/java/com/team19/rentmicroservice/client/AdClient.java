package com.team19.rentmicroservice.client;

import com.team19.rentmicroservice.dto.AdDTOSimple;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name="ad-service")
public interface AdClient {

    @GetMapping(value = "api/getAd/{id}", produces = "application/json")
    AdDTOSimple getAd(@PathVariable("id") Long id, @RequestHeader("permissions") String permissions,
                      @RequestHeader("userID") String userId, @RequestHeader("Authorization") String token);
}
