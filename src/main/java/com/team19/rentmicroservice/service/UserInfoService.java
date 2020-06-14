package com.team19.rentmicroservice.service;

import com.team19.rentmicroservice.model.UserInfo;

public interface UserInfoService {

    UserInfo findUserInfoByUserId(Long userId);
    UserInfo saveUserInfo(UserInfo userInfo);
}
