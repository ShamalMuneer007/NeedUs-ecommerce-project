package com.needus.ecommerce.repository.product;

import com.needus.ecommerce.entity.product.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<Coupon,Long>{
    List<Coupon> findByIsDeletedFalse();

    boolean existsByCouponName(String couponName);

    boolean existsByCouponCode(String couponCode);

    List<Coupon> findByIsValidTrueAndExpirationDateBefore(LocalDate now);

    boolean existsByCouponNameAndIsDeletedIsFalse(String couponName);

    boolean existsByCouponCodeAndIsDeletedIsFalse(String couponCode);

    List<Coupon> findByIsDeletedFalseAndIsValidTrue();
}
