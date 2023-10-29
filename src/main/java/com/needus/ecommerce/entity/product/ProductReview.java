package com.needus.ecommerce.entity.product;

import com.needus.ecommerce.entity.user.UserInformation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductReview {
    @ManyToOne
    UserInformation userInformation;
    @ManyToOne
    Products product;
    int rating = 1;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Lob
    private String reviewContent;
    @OneToMany(mappedBy = "productReview",cascade = {CascadeType.ALL},orphanRemoval = true)
    private List<ReviewComment> reviewComments;

    public int getEmptyStars() {
        return  5 - rating;
    }
}
