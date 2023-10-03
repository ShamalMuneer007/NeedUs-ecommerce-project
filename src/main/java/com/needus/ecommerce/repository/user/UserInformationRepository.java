package com.needus.ecommerce.repository.user;

import com.needus.ecommerce.entity.user.UserInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserInformationRepository extends JpaRepository<UserInformation, UUID> {
    public boolean existsByUsername(String username);
    public UserInformation findByUsername(String username);

    public boolean existsByEmail(String username);

    @Query(value = "SELECT * FROM Users WHERE is_deleted = false",nativeQuery = true)
    public List<UserInformation> findAllNonDeleted();

    UserInformation findByEmail(String email);
}
