package com.customerproduct.service;

import com.customerproduct.cache.ProductCache;
import com.customerproduct.dto.ProductSearchDTO;
import com.customerproduct.model.Customer;
import com.customerproduct.model.Product;
import com.customerproduct.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductServiceImpl implements ProductService{

    private final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);


    private final ProductCache productCache;


    private final ProductRepository productRepository;

    @Override
    public boolean addOrUpdateProduct(Product product) {
        try {
            Product product_redis= productRepository.addOrUpdateProduct(product);
            productCache.putProductsWithClientIdInBucket(product_redis);
            return true;
        } catch (Exception e) {
            log.error("Exception occurred at addOrUpdateCustomer() Method " ,e);
        }
        return false;
    }

    @Override
    public List<Product> getAllProducts(ProductSearchDTO productSearchDTO) {
        try {
            return productRepository.getAllProducts(productSearchDTO);
        } catch (Exception e) {
            log.error("Exception occurred at getAllProducts() Method " , e);
        }
        return null;
    }

    @Override
    public List<Product> getProduct(ProductSearchDTO productSearchDTO) {
        try {
            return productRepository.getProduct(productSearchDTO);
        } catch (Exception e) {
            log.error("Exception occurred at getProducts() Method ",e);
        }
        return null;
    }
}
