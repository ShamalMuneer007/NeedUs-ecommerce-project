package com.needus.ecommerce.repository.user;

import com.needus.ecommerce.entity.user.ConfirmationToken;
import com.needus.ecommerce.entity.user.UserInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("confirmationTokenRepository")
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
    ConfirmationToken findByToken(String Token);
    ConfirmationToken findByUserInformation(UserInformation userInformation);
}
