package com.needus.ecommerce.model;

import com.needus.ecommerce.entity.user.UserInformation;
import jakarta.persistence.*;

public class UserAddressDto {

    UserInformation userInformation;

    private long id;

    private String firstName;

    private String lastName;
    private String company;

    private String streetAddress;

    private String city;

    private String state;

    private Integer postalCode;

    private Integer contactNumber;
}
