package com.needus.ecommerce.entity.user;

import com.needus.ecommerce.entity.user.UserInformation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmationToken {

    @OneToOne(cascade = {CascadeType.DETACH , CascadeType.MERGE , CascadeType.PERSIST , CascadeType.REFRESH })
    @JoinColumn(name = "user_id",referencedColumnName = "user_id")
    UserInformation userInformation;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="token_id")
    private Long tokenId;
    private String token;
    @Column(name="expiry_date")
    private Timestamp expiryDate;
    public ConfirmationToken(String token,UserInformation userInformation){
        this.token = token;
        this.userInformation = userInformation;
    }
}
