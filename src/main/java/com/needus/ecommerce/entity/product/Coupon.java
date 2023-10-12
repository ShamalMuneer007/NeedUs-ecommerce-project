package com.needus.ecommerce.entity.product;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Data
@Getter
@Setter
public class Coupon {
    @Id
    @SequenceGenerator(name = "COUPON_SEQ",allocationSize = 110)
    @GeneratedValue(generator = "COUPON_SEQ",strategy = GenerationType.SEQUENCE)
    private long couponId;
    private String couponCode;
    private float couponDiscount;
    private LocalDate expiration_date;
}
