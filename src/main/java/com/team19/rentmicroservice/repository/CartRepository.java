package com.team19.rentmicroservice.repository;

import com.team19.rentmicroservice.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart,Long> {

    Cart findByClientID(Long id);
}
