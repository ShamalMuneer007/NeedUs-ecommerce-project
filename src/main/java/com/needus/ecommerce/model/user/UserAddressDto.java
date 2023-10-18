package com.needus.ecommerce.model.user;

import com.needus.ecommerce.entity.user.UserAddress;
import com.needus.ecommerce.entity.user.UserInformation;
import jakarta.persistence.*;
import lombok.*;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAddressDto {

    UserInformation userInformation;

    private long id;

    private String firstName;

    private String lastName;
    private String company;

    private String streetAddress;

    private String city;

    private String state;

    private String postalCode;

    private String contactNumber;

    public UserAddressDto(UserAddress address){
        this.userInformation = address.getUserInformation();
        this.id = address.getId();
        this.firstName = address.getFirstName();
        this.lastName = address.getLastName();
        this.company = address.getCompany();
        this.streetAddress = address.getStreetAddress();
        this.city = address.getCity();
        this.state = address.getState();
        this.postalCode = address.getPostalCode();
        this.contactNumber = address.getContactNumber();
    }
}
