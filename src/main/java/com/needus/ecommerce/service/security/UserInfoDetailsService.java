package com.needus.ecommerce.service.security;

import com.needus.ecommerce.entity.user.UserInformation;
import com.needus.ecommerce.repository.user.UserInformationRepository;
import com.needus.ecommerce.service.security.UserInfoDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
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
       if(user == null||user.isDeleted()){
           throw new UsernameNotFoundException("User not found");
       }
       if(!user.isEnabled()){
           throw new DisabledException("User is disabled");
       }
       return new UserInfoDetails(user);
    }
}
