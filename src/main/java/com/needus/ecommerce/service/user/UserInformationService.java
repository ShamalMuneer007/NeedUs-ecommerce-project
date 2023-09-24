package com.needus.ecommerce.service.user;


import com.needus.ecommerce.entity.user.UserInformation;

import java.util.List;
import java.util.UUID;

public interface UserInformationService {
    public UserInformation save(UserInformation user);
    public UserInformation register(UserInformation user);

    public List<UserInformation> findAllUsers();

    UserInformation findUserById(UUID id);

    void blockUser(UUID id);

    void deleteUserById(UUID id);

    void updateUser(UUID id, UserInformation user);

    UserInformation findUserByName(String username);
}
