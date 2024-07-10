package com.customerproduct.service;

import com.customerproduct.dto.ProductSearchDTO;
import com.customerproduct.model.Product;

import java.util.List;

public interface ProductService {
    boolean addOrUpdateProduct(Product product);
    List<Product> getAllProducts(ProductSearchDTO productSearchDTO);
    List<Product>  getProduct(ProductSearchDTO productSearchDTO);
}
