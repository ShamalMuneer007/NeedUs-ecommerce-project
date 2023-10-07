package com.needus.ecommerce.model;

import com.needus.ecommerce.entity.user.enums.Role;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String username;
    private String email;
    private String password;
    private Role role = Role.USER;
    private LocalDateTime userCreatedAt;
}
