package com.needus.ecommerce.service.user;

public interface UserLogService {
    void logUser(String username,String remoteAddress);
    Long totalVisitsCount();
}
