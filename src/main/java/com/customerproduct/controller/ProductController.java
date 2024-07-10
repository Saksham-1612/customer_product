package com.customerproduct.controller;


import com.customerproduct.dto.ProductSearchDTO;
import com.customerproduct.model.Customer;
import com.customerproduct.model.Product;
import com.customerproduct.service.KafkaProducerService;
import com.customerproduct.service.ProductService;
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
@RequestMapping("/product")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductController {

    private final Logger log = LoggerFactory.getLogger(CustomerController.class);

    private final ProductService productService;

    private final KafkaProducerService kafkaProducerService;


    @Operation(summary = "Add or update a Product", description = "This endpoint adds or updates a product")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Product added/updated successfully", content = @Content(mediaType = "application/json")), @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json"))})
    @PostMapping(value = "add-product")
    public ResponseEntity<?> addOrUpdateProduct(@Parameter(description = "Product to add or update", required = true) @RequestBody Product product) {
        Map<String, Object> response;
        try {
            Validator.isValidProduct(product);
            boolean productAddedOrUpdated= productService.addOrUpdateProduct(product);
            if (productAddedOrUpdated) {
                response = CustomerProductUtils.setResponse("Product added/updated successfully", null);
            } else {
                response = CustomerProductUtils.setResponse("Product added/updated failed", null);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error occurred at addOrUpdateProduct() ", e);
            response = CustomerProductUtils.setResponse("Product added/updated failed" + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "Get all Product", description = "This endpoint retrieves all products")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Products found", content = @Content(mediaType = "application/json")), @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(mediaType = "application/json"))})
    @PostMapping(value = "/get-all-products")
    public ResponseEntity<?> getAllProducts(@Parameter(description = "Search criteria for retrieving product")  @RequestBody ProductSearchDTO productSearchDTO) {
        try {
            log.info("Getting all products with SearchDTO{}",productSearchDTO);
            List<Product> products=productService.getAllProducts(productSearchDTO);
            if (!products.isEmpty()) {
                return ResponseEntity.ok(CustomerProductUtils.setResponse("Products found", products));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CustomerProductUtils.setResponse("Products not found ", null));
            }
        } catch (Exception e) {
            log.error("Error occurred at getALlProducts() Method ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomerProductUtils.setResponse("Internal Server Error " + e.getMessage(), null));
        }
    }

    @Operation(summary = "Get a Product", description = "This endpoint retrieves products with features")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Product found", content = @Content(mediaType = "application/json")), @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(mediaType = "application/json"))})
    @PostMapping(value = "/get-product")
    public ResponseEntity<?> getProduct(@Parameter(description = "Search criteria for retrieving a product", required = true) @RequestBody ProductSearchDTO productSearchDTO) {
        try {
            List<Product> products=productService.getProduct(productSearchDTO);
            if (!products.isEmpty()) {
                return ResponseEntity.ok(CustomerProductUtils.setResponse("Products found", products));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CustomerProductUtils.setResponse("No Product found", null));
            }
        } catch (Exception e) {
            log.error("Error occurred at getProduct() ", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CustomerProductUtils.setResponse("Error Occurred at getProduct() " + e.getMessage(), null));
        }
    }

    @Operation(summary = "Bulk add or update products", description = "This endpoint initiates bulk add or update of products")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Bulk add or update initiated", content = @Content(mediaType = "application/json")), @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json"))})
    @PostMapping(value = "/add-product-bulk")
    public ResponseEntity<?> bulkAddOrUpdateProduct(@Parameter(description = "List of products to add or update in bulk", required = true) @RequestBody List<Product> products) {
        try {
            boolean productAddedUpdatedSuccess=kafkaProducerService.bulkAddOrUpdateProduct(products);
            log.info("Product Added or Updated : {}", productAddedUpdatedSuccess);
            if (productAddedUpdatedSuccess) {
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
