package com.customerproduct.repository;

import com.customerproduct.dto.ProductSearchDTO;
import com.customerproduct.model.Product;

import java.util.List;

public interface ProductRepository {
    Product addOrUpdateProduct(Product product);
    List<Product> getAllProducts(ProductSearchDTO productSearchDTO);
    List<Product>  getProduct(ProductSearchDTO productSearchDTO);
}
