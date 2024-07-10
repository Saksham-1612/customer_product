package com.customerproduct.controller;

import com.customerproduct.dto.SearchDto;
import com.customerproduct.model.Customer;
import com.customerproduct.service.CustomerService;
import com.customerproduct.service.KafkaProducerService;
import com.customerproduct.utils.CustomerProductUtils;
import com.customerproduct.validator.Validator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CustomerController {

    private final Logger log = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerService customerService;

    private final KafkaProducerService kafkaProducerService;

    @Operation(summary = "Add or update a customer", description = "This endpoint adds or updates a customer")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Customer added/updated successfully", content = @Content(mediaType = "application/json")), @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json"))})
    @PostMapping(value = "add-customer")
    public ResponseEntity<?> addOrUpdateCustomer(@Parameter(description = "Customer to add or update", required = true) @RequestBody Customer customer) {
        Map<String, Object> response;
        try {
            Validator.isValidCustomer(customer);
            boolean customerAddedOrUpdated = customerService.addOrUpdateCustomer(customer);
            if (customerAddedOrUpdated) {
                response = CustomerProductUtils.setResponse("Customer added/updated successfully", null);
            } else {
                response = CustomerProductUtils.setResponse("Customer added/updated failed", null);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error occurred at addOrUpdateCustomer() ", e);
            response = CustomerProductUtils.setResponse("Customer added/updated failed" + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "Get all customers", description = "This endpoint retrieves all customers")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Customers found", content = @Content(mediaType = "application/json")), @ApiResponse(responseCode = "404", description = "Customers not found", content = @Content(mediaType = "application/json"))})
    @PostMapping(value = "/get-customers")
    public ResponseEntity<?> getAllCustomers(@Parameter(description = "Search criteria for retrieving customers") @RequestBody SearchDto searchDto) {
        try {
            Validator.validateGetAllCustomersParameters(searchDto);
            log.info("Search DTO {}", searchDto);
            List<Customer> customers = customerService.getAllCustomers(searchDto);
            if (!customers.isEmpty()) {
                return ResponseEntity.ok(CustomerProductUtils.setResponse("Customer found", customers));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CustomerProductUtils.setResponse("Customer not found ", null));
            }
        } catch (Exception e) {
            log.error("Error occurred at getAllCustomer() Method ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomerProductUtils.setResponse("Internal Server Error " + e.getMessage(), null));
        }
    }

    @Operation(summary = "Get a customer", description = "This endpoint retrieves a customer")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Customer found", content = @Content(mediaType = "application/json")), @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content(mediaType = "application/json"))})
    @PostMapping(value = "/get-customer")
    public ResponseEntity<?> getCustomer(@Parameter(description = "Search criteria for retrieving a customer", required = true) @RequestBody SearchDto searchDto) {
        try {
            Validator.validateGetCustomer(searchDto);
            List<Customer> customers = customerService.getCustomer(searchDto);
            if (!customers.isEmpty()) {
                return ResponseEntity.ok(CustomerProductUtils.setResponse("Customers found", customers));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CustomerProductUtils.setResponse("No Customer found", null));
            }
        } catch (Exception e) {
            log.error("Error occurred at getCustomer() ", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CustomerProductUtils.setResponse("Error Occurred at getCustomer() " + e.getMessage(), null));
        }
    }

    @Operation(summary = "Bulk add or update customers", description = "This endpoint initiates bulk add or update of customers")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Bulk add or update initiated", content = @Content(mediaType = "application/json")), @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json"))})
    @PostMapping(value = "/add-customer-bulk")
    public ResponseEntity<?> bulkAddOrUpdateCustomer(@Parameter(description = "List of customers to add or update in bulk", required = true) @RequestBody List<Customer> customers) {
        try {
            boolean customerAddedUpdatedSuccess = kafkaProducerService.bulkAddOrUpdateCustomer(customers);
            log.info("Customer Added or Updated : {}", customerAddedUpdatedSuccess);
            if (customerAddedUpdatedSuccess) {
                return new ResponseEntity<>(CustomerProductUtils.setResponse("Bulk Add or Update Initiated", null), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(CustomerProductUtils.setResponse("Something went wrong while Bulk Add or Update ", null), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            log.error("Error occurred at bulkAddOrUpdateCustomer() ", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CustomerProductUtils.setResponse("Internal Server Error " + e.getMessage(), null));
        }
    }
}
