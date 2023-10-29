package com.needus.ecommerce.repository.user;

import com.needus.ecommerce.entity.user.UserLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLogRepository extends JpaRepository<UserLog,Long>{

}
