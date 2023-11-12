package com.needus.ecommerce.entity.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAddress {
    @ManyToOne
    @JoinColumn(name="userId")
    UserInformation userInformation;
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ADDRESS_SEQ")
    @SequenceGenerator(name="ADDRESS_SEQ", sequenceName="ADDRESS_SEQ", allocationSize=99)
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
    private String postalCode;
    @Column(nullable = false)
    private String contactNumber;
    private boolean isDeleted = false;
}
