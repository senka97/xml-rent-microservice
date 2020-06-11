package com.team19.rentmicroservice.client;

import com.team19.rentmicroservice.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@FeignClient(name = "ad-service")
public interface AdClient {

    @GetMapping("api/ads/{id}/owner")
    AdOwnerDTO getAdOwner(@PathVariable("id") Long id, @RequestHeader("permissions") String permissions,
               @RequestHeader("userID") String userId, @RequestHeader("Authorization") String token);

    @PostMapping("api/ads/cartItems")
    List<CartItemDTO> findAds(@RequestBody List<CartItemDTO> cartItems, @RequestHeader("permissions") String permissions,
                              @RequestHeader("userID") String userId, @RequestHeader("Authorization") String token);

    @GetMapping(value = "api/getAd/{id}", produces = "application/json")
    AdDTOSimple getAd(@PathVariable("id") Long id, @RequestHeader("permissions") String permissions,
                      @RequestHeader("userID") String userId, @RequestHeader("Authorization") String token);

    @PostMapping("api/priceList/ads")
    List<PriceListAdDTO> findPrices(@RequestBody List<PriceListAdDTO> priceListAdDTOs, @RequestHeader("permissions") String permissions,
                                    @RequestHeader("userID") String userId, @RequestHeader("Authorization") String token);

    @PostMapping("api/ads/fill")
    List<AdFrontDTO> fillAdsWithInformation(@RequestBody List<Long> adIDs, @RequestHeader("permissions") String permissions,
                                            @RequestHeader("userID") String userId, @RequestHeader("Authorization") String token);

    @PostMapping(value = "api/userCanPostComment/{adId}/{uId}/{endDate}")
    Boolean createUserCanPostComment(@PathVariable("adId") Long adId, @PathVariable("uId") Long uId, @PathVariable("endDate") String endDate, @RequestHeader("permissions") String permissions,
                                     @RequestHeader("userID") String userId, @RequestHeader("Authorization") String token);

    @PostMapping(value = "rating/user/{userId}/ad/{adId}/{endDate}")
    boolean createUserCanRate(@PathVariable("userId") Long userId,
                              @PathVariable("adId") Long adId,
                              @PathVariable("endDate") String endDate,
                              @RequestHeader("permissions") String permissions,
                              @RequestHeader("userID") String userID,
                              @RequestHeader("Authorization") String token);


}
