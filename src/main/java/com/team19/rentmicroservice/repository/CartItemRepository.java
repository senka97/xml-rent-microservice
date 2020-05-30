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

    List<CartItem> findByCartAndInCart(Cart cart, boolean inCart);

    //proverim za sve cartItem iz korpe klijenta da li postoji neki sa datim oglasom a koji se preklapa sa novim cartItem
    @Query(value="FROM CartItem ci inner join ci.cart c WHERE c.clientID=?1 AND ci.adID=?2 AND ci.inCart=true AND ((ci.startDate<?3 AND ci.endDate>=?3) OR (ci.startDate>=?3 AND ci.endDate<=?4) OR (ci.startDate<=?4 AND ci.endDate>?4) OR (ci.startDate<?3 AND ci.endDate>?4))")
    List<CartItem> findCartItemsForAd(Long clientID, Long adID, LocalDate startDate, LocalDate endDate);
}
