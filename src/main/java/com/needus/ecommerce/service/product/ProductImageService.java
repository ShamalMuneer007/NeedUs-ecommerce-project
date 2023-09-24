package com.needus.ecommerce.service.product;

import com.needus.ecommerce.entity.product.ProductImages;
import org.springframework.web.multipart.MultipartFile;

public interface ProductImageService {
    ProductImages save(ProductImages productImage);
}
