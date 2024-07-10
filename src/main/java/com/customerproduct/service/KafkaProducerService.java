package com.customerproduct.service;

import com.customerproduct.constants.AppConstants;
import com.customerproduct.model.Customer;
import com.customerproduct.model.Product;
import com.customerproduct.validator.Validator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class KafkaProducerService {

    private final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);


    private final KafkaTemplate<String, Object> kafkaTemplate;

    public boolean bulkAddOrUpdateCustomer(List<Customer> customers) {
        try {
            for (Customer customer : customers) {
                try {
                    Validator.isValidCustomer(customer);
                    kafkaTemplate.send(AppConstants.CUSTOMER_ADD_UPDATE_TOPIC_NAME, customer);
                } catch (Exception e) {
                    log.error("Exception occurred at bulkAddOrUpdate() Method in Kafka service ", e);
                }
            }
            return true;
        } catch (Exception e) {
            log.error("Exception occurred at bulkAddOrUpdate() Method in Kafka service ", e);
        }
        return false;
    }

    public boolean bulkAddOrUpdateProduct(List<Product> products) {
        try {
            for (Product product:products) {
                try {
                    Validator.isValidProduct(product);
                    kafkaTemplate.send(AppConstants.PRODUCT_ADD_UPDATE_TOPIC_NAME, product);
                } catch (Exception e) {
                    log.error("Exception occurred at bulkAddOrUpdate() Method in Kafka service ", e);
                }
            }
            return true;
        } catch (Exception e) {
            log.error("Exception occurred at bulkAddOrUpdate() Method in Kafka service ", e);
        }
        return false;
    }
}
