package com.needus.ecommerce.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.Date;
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
//    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String username;
    @Column(name = "phoneNumber")
    private String phoneNumber;
    @Column(nullable = true)
    private String password;
    @Column(nullable = false)
    private LocalDateTime userCreatedAt;
    private boolean isEnabled = true;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;
}
