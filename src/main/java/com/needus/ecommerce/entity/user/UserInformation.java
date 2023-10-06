package com.needus.ecommerce.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
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
    private List<UserAddress> userAddresses;
}
