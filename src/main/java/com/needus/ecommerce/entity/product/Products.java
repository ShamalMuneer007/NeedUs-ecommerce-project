package com.needus.ecommerce.entity.product;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.needus.ecommerce.entity.user.UserInformation;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Products {

    @ManyToMany(cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @JoinTable(name="filter_tags",
            joinColumns = @JoinColumn(name = "productId"),
            inverseJoinColumns = @JoinColumn(name = "filterId")
    )
    @JsonManagedReference
    public List<ProductFilters> productFilters;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="productId")
    private Long productId;
    @Column(name = "product_name")
    private String productName;
    @Lob
    @Column(name="description", length=512)
    private String description;
    private Float productPrice;
    private Float productBasePrice;
    private LocalDate discountOfferExpiryDate = null;
    private boolean isDiscountOfferExpired;
    @Column(name="product_status")
    private boolean productStatus;
    private Integer stock;
    private boolean isDeleted = false;
    @Column(name = "average_rating")
    private int averageRating = 1;
    @ManyToOne
    @JoinColumn(name="sellerId",unique = false)
    private UserInformation userInformation;
    @OneToMany(mappedBy = "product")
    @JsonManagedReference
    private List<ProductReview> productReview;
    @CreatedDate
    private LocalDateTime publishedAt;
    @OneToMany(mappedBy = "product", cascade = {CascadeType.REMOVE,CascadeType.PERSIST,CascadeType.MERGE})
    @JsonManagedReference
    private List<ProductImages> images;
    @ManyToOne
    @JoinColumn(name="category_id",unique = false)
    private Categories categories;
    @ManyToOne
    @JoinColumn(name="brand_id")
    private Brands brands;
}
