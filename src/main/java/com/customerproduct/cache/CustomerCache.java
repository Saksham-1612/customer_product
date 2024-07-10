package com.customerproduct.cache;

import com.customerproduct.constants.AppConstants;
import com.customerproduct.dto.SearchDto;
import com.customerproduct.model.Customer;
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
public class CustomerCache {

    private final Logger log = LoggerFactory.getLogger(CustomerCache.class);

    private final RedissonClient redissonClient;

    public List<Customer> getCustomerListFromBucket(SearchDto searchDto) {
        String key = AppConstants.CUSTOMER_CACHE_PREFIX + searchDto.getClient();
        RBucket<List<Customer>> bucket = redissonClient.getBucket(key);
        List<Customer> customers = bucket.get();
        if (customers == null) {
            return Collections.emptyList();
        }

        int offset = searchDto.getOffset();
        int limit = searchDto.getLimit();

        int fromIndex = Math.min(offset, customers.size());
        int toIndex = Math.min(fromIndex + limit, customers.size());

        return customers.subList(fromIndex, toIndex);
    }

    public void putCustomersWithClientIdInBucket(Customer customer) {
        if (customer == null) return;
        String key = AppConstants.CUSTOMER_CACHE_PREFIX + customer.getClient();
        RBucket<List<Customer>> bucket = redissonClient.getBucket(key);
        List<Customer> customers = bucket.get();

        if (customers == null) {
            customers = new ArrayList<>();
        }

        boolean updated = false;
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getId() == customer.getId()) {
                customers.set(i, customer); // Update existing customer
                updated = true;
                break;
            }
        }

        if (!updated) {
            customers.add(customer); // Add new customer
        }
        bucket.set(customers, Duration.ofSeconds(AppConstants.CACHE_TTL_SECONDS));
        log.info("Customer {} in Redisson bucket", updated ? "updated" : "added");
    }
}
