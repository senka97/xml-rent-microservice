package com.team19.rentmicroservice.repository;

import com.team19.rentmicroservice.model.Cart;
import com.team19.rentmicroservice.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {

    CartItem findByAdIDAndStartDateAndEndDateAndCart(Long adID, LocalDate startDate, LocalDate endDate, Cart cart);
}
