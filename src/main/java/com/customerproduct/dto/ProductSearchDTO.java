package com.customerproduct.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ProductSearchDTO {
    private int id;
    private int emp_id;
    private int client_id;
    private String name;
    private String skuCode;
    private Timestamp last_modified_date;
    private boolean enable;
    private Integer limit;
    private Integer offset;
}
