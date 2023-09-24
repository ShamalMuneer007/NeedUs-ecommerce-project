package com.needus.ecommerce.model;

import com.needus.ecommerce.entity.user.Role;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String username;
    private String email;
    private String password;
    private Role role = Role.USER;
    private LocalDateTime userCreatedAt;
}
