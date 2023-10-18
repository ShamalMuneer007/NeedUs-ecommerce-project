package com.needus.ecommerce.entity.user;

import com.needus.ecommerce.entity.user_order.UserOrder;
import com.needus.ecommerce.entity.user.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@ToString(exclude = {"userOrders"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="Users")
public class UserInformation{
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;
    @NonNull
    private String email;
    @Column(nullable = false)
    private String username;
    @Column(name = "phoneNumber")
    private String phoneNumber;
    @Column(nullable = true)
    private String password;
    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime userCreatedAt;
    @Column(name = "isEnabled")
    private boolean isEnabled = true;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToOne
    @JoinColumn(name = "user_wishlist")
    private Wishlist userWishlist;
    @OneToOne
    @JoinColumn(name = "user_cart")
    private Cart cart;
    @Column(nullable = false,name = "isDeleted")
    private boolean isDeleted = false;
    @OneToMany(mappedBy = "userInformation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAddress> userAddresses = new LinkedList<>();
    @OneToMany(mappedBy = "userInformation",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<UserOrder> userOrders = new HashSet<>();
    @OneToOne(mappedBy = "userInformation", cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="wallet_id")
    private Wallet wallet;
}
