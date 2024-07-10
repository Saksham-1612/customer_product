package com.customerproduct.service;

import com.customerproduct.dto.SearchDto;
import com.customerproduct.model.Customer;

import java.util.List;

public interface CustomerService {
    boolean addOrUpdateCustomer(Customer customer);

    List<Customer> getAllCustomers(SearchDto searchDto);

    List<Customer> getCustomer(SearchDto searchDto);
}
