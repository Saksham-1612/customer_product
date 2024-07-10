package com.customerproduct.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class SearchDto {
    private int id;
    private Integer client;
    private String name;
    private Timestamp lastModifiedDate;
    private Timestamp create_date;
    private String phoneNo;
    private String customerCode;
    private String email;
    private Integer limit;
    private Integer offset;
}
