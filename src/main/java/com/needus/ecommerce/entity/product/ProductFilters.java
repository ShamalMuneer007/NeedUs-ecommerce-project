package com.needus.ecommerce.entity.product;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductFilters {
    @Id
    @SequenceGenerator(name = "filterSequence",allocationSize = 1)
    @GeneratedValue(generator = "filterSequence",strategy = GenerationType.SEQUENCE)
    @Column(name = "filterId")
    private Integer filterId;
    @Column(name = "filterName")
    private String filterName;
    @ManyToOne
    @JoinColumn(name="categoryId")
    private Categories category;

}
