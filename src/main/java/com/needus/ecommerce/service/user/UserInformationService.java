package com.needus.ecommerce.service.user;


import com.needus.ecommerce.entity.user.UserInformation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.UUID;

public interface UserInformationService {
    public UserInformation save(UserInformation user);
    public UserInformation register(UserInformation user);

    public UserInformation findUserById(UUID id);

//    void blockUser(UUID id);

    public void blockUser(UUID id, HttpServletRequest request, HttpServletResponse response);

    public void deleteUserById(UUID id);

    public void updateUser(UUID id, UserInformation user);

    public UserInformation findUserByName(String username);

    public List<UserInformation> findAllUsers();

    public boolean usersExistsByUsername(String username);
    public UserInformation getCurrentUser();

    void changePassword(UserInformation user, String password);
}
