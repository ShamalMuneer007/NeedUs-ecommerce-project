package com.needus.ecommerce.service.product;

import com.needus.ecommerce.entity.product.ProductImages;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductImageService {
    ProductImages save(ProductImages productImage);

    boolean existsById(Long imageId);

    ProductImages findImageById(Long imageId);

    void removeProductImages(List<ProductImages> images);
}
