package com.team19.rentmicroservice.repository;

import com.team19.rentmicroservice.model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

    UserInfo findUserInfoByUserId(Long userId);
}
