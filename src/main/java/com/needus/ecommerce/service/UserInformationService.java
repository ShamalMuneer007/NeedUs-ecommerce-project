package com.needus.ecommerce.service;


import com.needus.ecommerce.entity.UserInformation;
import com.needus.ecommerce.model.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

public interface UserInformationService {
    public UserInformation save(UserInformation user);
    public UserInformation register(UserInformation user);

}
