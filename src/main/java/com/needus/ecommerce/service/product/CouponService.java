package com.needus.ecommerce.service.product;

import com.needus.ecommerce.entity.product.Coupon;
import com.needus.ecommerce.entity.user.Cart;
import com.needus.ecommerce.entity.user.UserInformation;

import java.util.List;

public interface CouponService {
    float couponDiscount(Cart cart);

    List<Coupon> findAllNonDeletedCoupons();

    boolean couponNameExists(String couponName);

    boolean couponCodeExists(String couponCode);

    void addCoupon(Coupon coupon);

    void deleteCoupon(Long couponId);

    Coupon findById(Long couponId);

    List<Coupon> findAllUserUsedCoupon(UserInformation user);
}
