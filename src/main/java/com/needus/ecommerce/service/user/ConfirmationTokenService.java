package com.needus.ecommerce.service.user;

import com.needus.ecommerce.entity.user.ConfirmationToken;
import com.needus.ecommerce.entity.user.UserInformation;
import com.needus.ecommerce.repository.user.ConfirmationTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Calendar;

@Service
public class ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    public ConfirmationTokenService(ConfirmationTokenRepository confirmationTokenRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    @Transactional
    public ConfirmationToken findByToken(String token){
        return confirmationTokenRepository.findByToken(token);
    }
    @Transactional
    public ConfirmationToken findByUser(UserInformation userInformation){
        return confirmationTokenRepository.findByUserInformation(userInformation);
    }
    public void save(UserInformation userInformation , String token){
        ConfirmationToken confirmationToken = new ConfirmationToken(token,userInformation);
        confirmationToken.setExpiryDate(calculateTime(24*60*60));
        confirmationTokenRepository.save(confirmationToken);

    }
    private Timestamp calculateTime(int expiryTimeInMinutes){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE,expiryTimeInMinutes);
        return new Timestamp(cal.getTime().getTime());
    }
}
