package com.needus.ecommerce.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAddress {
    @ManyToOne
    @JoinColumn(name="userId")
    UserInformation userInformation;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    private String company;
    @Column(nullable = false)
    private String streetAddress;
    @Column(nullable = false)
    private String city;
    @Column(nullable = false)
    private String state;
    @Column(nullable = false)
    private Integer postalCode;
    @Column(nullable = false)
    private Integer contactNumber;
}
