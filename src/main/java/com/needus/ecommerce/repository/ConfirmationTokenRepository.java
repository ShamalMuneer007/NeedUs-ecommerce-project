package com.needus.ecommerce.repository;

import com.needus.ecommerce.entity.ConfirmationToken;
import com.needus.ecommerce.entity.UserInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("confirmationTokenRepository")
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
    ConfirmationToken findByToken(String Token);
    ConfirmationToken findByUserInformation(UserInformation userInformation);
}
