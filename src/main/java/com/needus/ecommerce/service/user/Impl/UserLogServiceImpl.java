package com.needus.ecommerce.service.user.Impl;

import com.needus.ecommerce.entity.user.UserLog;
import com.needus.ecommerce.repository.user.UserLogRepository;
import com.needus.ecommerce.service.user.UserLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserLogServiceImpl implements UserLogService {

    @Autowired
    UserLogRepository userLogRepository;
    @Override
    public void logUser(String username, String remoteAddress) {
        UserLog userLog =  new UserLog();
        userLog.setUsername(username);
        userLog.setIpAddress(remoteAddress);
        userLogRepository.save(userLog);
    }

    @Override
    public Long totalVisitsCount() {
        return userLogRepository.count();
    }
}
