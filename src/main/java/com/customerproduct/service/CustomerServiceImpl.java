package com.customerproduct.service;

import com.customerproduct.cache.CustomerCache;
import com.customerproduct.constants.AppConstants;
import com.customerproduct.dto.SearchDto;
import com.customerproduct.model.Customer;
import com.customerproduct.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CustomerServiceImpl implements CustomerService {

    private final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);


    private final CustomerCache customerCache;

    private final CustomerRepository customerRepository;

    private final RedissonClient redissonClient;

    public boolean addOrUpdateCustomer(Customer customer) {
        try {
            Customer redisCustomer= customerRepository.addOrUpdateCustomer(customer);
            customerCache.putCustomersWithClientIdInBucket(redisCustomer);
            return true;
        } catch (Exception e) {
            log.error("Exception occurred at addOrUpdateCustomer() Method " ,e);
        }
        return false;
    }

    public List<Customer> getAllCustomers(SearchDto searchDto) {
        try {
            List<Customer> redisCustomer = customerCache.getCustomerListFromBucket(searchDto);
            log.info("Customers in redis is {}",redisCustomer);
            if(redisCustomer == null || redisCustomer.isEmpty()) {
                return customerRepository.getAllCustomers(searchDto);
            }
            return redisCustomer;
        } catch (Exception e) {
            log.error("Exception occurred at getAllCustomers() Method " , e);
        }
        return null;
    }

    public List<Customer> getCustomer(SearchDto searchDto) {
        try {
            return customerRepository.getCustomer(searchDto);
        } catch (Exception e) {
            log.error("Exception occurred at getCustomers() Method ",e);
        }
        return null;
    }
}
