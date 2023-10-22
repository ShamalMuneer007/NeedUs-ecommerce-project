package com.needus.ecommerce.entity.product;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;

@Entity
@Data
@Getter
@Setter
@Slf4j
public class Coupon {
    @Id
    @SequenceGenerator(name = "COUPON_SEQ",allocationSize = 110)
    @GeneratedValue(generator = "COUPON_SEQ",strategy = GenerationType.SEQUENCE)
    private Long couponId;
    private String couponName;
    private String couponCode;
    private String couponDescription;
    private boolean isDeleted = false;
    private boolean isValid = true;
    private Float maxPriceLimit;
    private Float couponDiscount;
    private Long minPriceLimit;
    private LocalDate expirationDate;
    private LocalDate createdDate;
}
