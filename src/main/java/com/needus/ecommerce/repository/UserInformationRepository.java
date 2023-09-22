package com.needus.ecommerce.repository;

import com.needus.ecommerce.entity.UserInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserInformationRepository extends JpaRepository<UserInformation, Long> {
    public boolean existsByUsername(String username);
    public UserInformation findByUsername(String username);

    public boolean existsByEmail(String username);
}
