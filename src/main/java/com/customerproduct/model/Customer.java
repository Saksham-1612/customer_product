package com.customerproduct.model;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "customer", uniqueConstraints = {@UniqueConstraint(columnNames = {"client", "customer_code"})})
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int client;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "last_modified_date")
    @UpdateTimestamp
    private Timestamp lastModifiedDate;

    @Column(name = "create_date", updatable = false, insertable = false)
    @CreationTimestamp
    private Timestamp createDate;

    @Column(name = "phone_no", length = 15)
    private String phoneNo;

    @Column(name = "customer_code", nullable = false, length = 50)
    private String customerCode;

    @Column(length = 50)
    private String email;

    @Column(nullable = false)
    private boolean enable;

}
