package com.needus.ecommerce.repository.user;

import com.needus.ecommerce.entity.user_order.UserOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserOrderRepository extends JpaRepository<UserOrder,Long> {
    Page<UserOrder> findByUserInformation_UserId(UUID userId, Pageable pageable);

    @Query(value = "SELECT * FROM user_order WHERE order_status = 'PENDING'",nativeQuery = true)
    List<UserOrder> findPendingOrder();

    @Query(value = "SELECT * FROM user_order WHERE order_status = 'DELIVERED'",nativeQuery = true)
    List<UserOrder> findDeliveredOrder();

    @Query(value = "SELECT * FROM user_order WHERE order_status = 'REFUNDED'",nativeQuery = true)
    List<UserOrder> findRefundedOrder();

    @Query(value = "SELECT * FROM user_order WHERE order_status = 'CANCELED'",nativeQuery = true)
    List<UserOrder> findCanceledOrder();
}
