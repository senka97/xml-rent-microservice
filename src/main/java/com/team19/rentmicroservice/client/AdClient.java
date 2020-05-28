package com.team19.rentmicroservice.client;

import com.team19.rentmicroservice.dto.AdDTO;
import com.team19.rentmicroservice.dto.CartItemDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "ad-service")
public interface AdClient {

    @GetMapping("api/ads/{id}/owner")
    Long getAd(@PathVariable("id") Long id, @RequestHeader("permissions") String permissions,
                @RequestHeader("userID") String userId, @RequestHeader("Authorization") String token);

    @PostMapping("api/ads/cartItems")
    List<CartItemDTO> findAds(@RequestBody List<CartItemDTO> cartItems, @RequestHeader("permissions") String permissions,
                              @RequestHeader("userID") String userId, @RequestHeader("Authorization") String token);
}
