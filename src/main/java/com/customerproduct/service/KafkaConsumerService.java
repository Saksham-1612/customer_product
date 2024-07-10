package com.customerproduct.service;

import com.customerproduct.cache.CustomerCache;
import com.customerproduct.cache.ProductCache;
import com.customerproduct.constants.AppConstants;
import com.customerproduct.model.Customer;
import com.customerproduct.model.Product;
import com.customerproduct.repository.CustomerRepository;

import com.customerproduct.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class KafkaConsumerService {

    private final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final CustomerRepository customerRepository;

    private final ProductRepository productRepository;

    private final CustomerCache customerCache;

    private final ProductCache productCache;



    @KafkaListener(topics = AppConstants.CUSTOMER_ADD_UPDATE_TOPIC_NAME, groupId = AppConstants.CUSTOMER_GROUP_ID, containerFactory = "customerProductListener")
    public boolean addOrUpdateCustomerBulk(Customer customer) {
        try {
            Customer redisCustomer = customerRepository.addOrUpdateCustomer(customer);
            customerCache.putCustomersWithClientIdInBucket(redisCustomer);
            return true;
        } catch (Exception e) {
            log.error("Exception occurred at bulkAddOrUpdateCustomer() in Customer Dao ", e);
        }
        return false;
    }

    @KafkaListener(topics = AppConstants.PRODUCT_ADD_UPDATE_TOPIC_NAME, groupId = AppConstants.PRODUCT_ADD_UPDATE_TOPIC_NAME, containerFactory = "customerProductListener")
    public boolean addOrUpdateProductBulk(Product product) {
        try {
            Product redisProduct=productRepository.addOrUpdateProduct(product);
            productCache.putProductsWithClientIdInBucket(redisProduct);
            return true;
        } catch (Exception e) {
            log.error("Exception occurred at bulkAddOrUpdateCustomer() in Kafka Consumer", e);
        }
        return false;
    }
}
