package com.needus.ecommerce.repository.product;

import com.needus.ecommerce.entity.product.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<Coupon,Long>{
}
