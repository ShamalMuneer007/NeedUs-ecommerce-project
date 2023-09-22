package com.needus.ecommerce.service;

import com.needus.ecommerce.entity.UserInformation;
import com.needus.ecommerce.repository.UserInformationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserInfoDetailsService implements UserDetailsService {
    @Autowired
    private UserInformationRepository userInformationRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
       UserInformation user = userInformationRepository.findByUsername(username);
       if(user == null){
           throw new UsernameNotFoundException("User not found");
       }
       if(!user.isEnabled()){
           throw new DisabledException("User is disabled");
       }
       return new UserInfoDetails(user);
    }
}
