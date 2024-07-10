package com.customerproduct.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.redis.core.RedisHash;

//import java.time.LocalDateTime;
import java.sql.Timestamp;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product",uniqueConstraints = {@UniqueConstraint(columnNames = {"client_id", "skuCode"})})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int client_id;

    @Column(nullable = false)
    private int emp_id;

    @Column(unique = true, length = 25)
    private String skuCode;

    @Column(nullable = false,length = 25)
    private String name;

    @UpdateTimestamp
    private Timestamp last_modified_date;

    @Column(nullable = false)
    private boolean enable;

}