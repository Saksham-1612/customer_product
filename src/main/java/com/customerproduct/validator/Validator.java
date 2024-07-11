package com.customerproduct.validator;

import com.customerproduct.dto.ProductSearchDTO;
import com.customerproduct.dto.SearchDto;
import com.customerproduct.exception.BadRequestException;
import com.customerproduct.model.Customer;
import com.customerproduct.model.Product;

import java.util.regex.Pattern;

public class Validator {

    private static final String PHONE_NUMBER_PATTERN = "\\+?[0-9]{10,15}";
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    public static void isPhoneNumberValid(String phoneNumber) throws BadRequestException {
        if (!Pattern.matches(PHONE_NUMBER_PATTERN, phoneNumber)) {
            throw new BadRequestException("Invalid phone number format");
        }
    }

    public static void isEmailValid(String email) throws BadRequestException {
        if (!Pattern.matches(EMAIL_PATTERN, email)) {
            throw new BadRequestException("Invalid email format");
        }
    }

    public static void isValidCustomer(Customer customer) throws BadRequestException {
        if (customer.getClient() <= 0) {
            throw new BadRequestException("Invalid client ID");
        }
        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            throw new BadRequestException("Name cannot be empty");
        }
        if (customer.getCustomerCode() == null || customer.getCustomerCode().trim().isEmpty()) {
            throw new BadRequestException("Customer code cannot be empty");
        }

        isPhoneNumberValid(customer.getPhoneNo());
        isEmailValid(customer.getEmail());
    }


    public static void validateGetAllCustomersParameters(SearchDto searchDto) throws BadRequestException {
        if (searchDto.getClient() == null || searchDto.getClient() <= 0) {
            throw new BadRequestException("Invalid or missing client ID: " + searchDto.getClient());
        }
        if (searchDto.getOffset() == null || searchDto.getOffset() < 0) {
            searchDto.setOffset(0);
        }
        if (searchDto.getLimit() == null || searchDto.getLimit() <= 0) {
            searchDto.setLimit(10);
        }
    }

    public static void validateGetCustomer(SearchDto searchDto) throws BadRequestException {
        if (searchDto.getClient() == null || searchDto.getClient() <= 0) {
            throw new BadRequestException("Invalid or missing client ID: " + searchDto.getClient());
        }
    }

    public static void isValidProduct(Product product) throws BadRequestException{
        if(product.getEmp_id()<0){
            throw new BadRequestException("Invalid Employee Id");
        }
        if(product.getClient_id()<0){
            throw new BadRequestException("Invalid Client Id");
        }
        if(product.getName()==null || product.getName().trim().isEmpty()){
            throw new BadRequestException("Invalid Name");
        }
        if(product.getSkuCode()==null || product.getSkuCode().trim().isEmpty() || product.getSkuCode().length()>25){
            throw new BadRequestException("Invalid SkuCode");
        }
    }
    public static void validateGetAllProductsParameters(ProductSearchDTO productSearchDTO) throws BadRequestException {
        if (productSearchDTO.getClient_id() == null || productSearchDTO.getClient_id() <= 0) {
            throw new BadRequestException("Invalid or missing client ID: " + productSearchDTO.getClient_id());
        }
        if (productSearchDTO.getOffset() == null || productSearchDTO.getOffset() < 0) {
            productSearchDTO.setOffset(0);
        }
        if (productSearchDTO.getLimit() == null || productSearchDTO.getLimit() <= 0) {
            productSearchDTO.setLimit(10);
        }
    }

}
