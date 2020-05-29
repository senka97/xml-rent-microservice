package com.team19.rentmicroservice.repository;

import com.team19.rentmicroservice.model.Cart;
import com.team19.rentmicroservice.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {

    CartItem findByAdIDAndStartDateAndEndDateAndCart(Long adID, LocalDate startDate, LocalDate endDate, Cart cart);

    @Query(value="FROM CartItem WHERE id IN ?1")
    List<CartItem> findCartItems(List<Long> cartItemsIDs);
}
