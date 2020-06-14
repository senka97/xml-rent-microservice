package com.team19.rentmicroservice.service.impl;

import com.team19.rentmicroservice.model.UserInfo;
import com.team19.rentmicroservice.repository.UserInfoRepository;
import com.team19.rentmicroservice.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Override
    public UserInfo findUserInfoByUserId(Long userId) {
        return userInfoRepository.findUserInfoByUserId(userId);
    }

    @Override
    public UserInfo saveUserInfo(UserInfo userInfo) {
        return this.userInfoRepository.save(userInfo);
    }
}
