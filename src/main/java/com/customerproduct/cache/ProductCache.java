package com.customerproduct.cache;

import com.customerproduct.constants.AppConstants;
import com.customerproduct.dto.ProductSearchDTO;
import com.customerproduct.dto.SearchDto;
import com.customerproduct.model.Customer;
import com.customerproduct.model.Product;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductCache {

    private final Logger log = LoggerFactory.getLogger(ProductCache.class);


    private final RedissonClient redissonClient;

    public List<Product> getProductListFromBucket(ProductSearchDTO productSearchDTO) {
        String key = AppConstants.PRODUCT_CACHE_PREFIX + productSearchDTO.getClient_id();
        RBucket<List<Product>> bucket = redissonClient.getBucket(key);
        List<Product> products = bucket.get();
        if (products == null) {
            return Collections.emptyList();
        }

        int offset = productSearchDTO.getOffset();
        int limit = productSearchDTO.getLimit();

        int fromIndex = Math.min(offset, products.size());
        int toIndex = Math.min(fromIndex + limit, products.size());

        return products.subList(fromIndex, toIndex);
    }

    public void putProductsWithClientIdInBucket(Product product) {
        if (product == null) return;
        String key = AppConstants.PRODUCT_CACHE_PREFIX + product.getClient_id();
        RBucket<List<Product>> bucket = redissonClient.getBucket(key);
        List<Product> products= bucket.get();

        if (products == null) {
            products = new ArrayList<>();
        }

        boolean updated = false;
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == product.getId()) {
                products.set(i, product); // Update existing customer
                updated = true;
                break;
            }
        }

        if (!updated) {
            products.add(product); // Add new customer
        }
        bucket.set(products, Duration.ofSeconds(AppConstants.CACHE_TTL_SECONDS));
        log.info("Product {} in Redisson bucket", updated ? "updated" : "added");
    }
}
