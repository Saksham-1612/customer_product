package com.customerproduct.dao;

import com.customerproduct.cache.CustomerCache;
import com.customerproduct.dto.SearchDto;
import com.customerproduct.model.Customer;
import com.customerproduct.repository.CustomerRepository;
import com.customerproduct.utils.CustomerProductUtils;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CustomerDao implements CustomerRepository {

    private final Logger log = LoggerFactory.getLogger(CustomerDao.class);


    private final CustomerCache customerCache;

    @Qualifier("entityManagerFactory")
    private final SessionFactory sessionFactory;

    @Override
    public Customer addOrUpdateCustomer(Customer customer) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            Customer addedCustomer=session.merge(customer);
            session.flush();
            session.refresh(addedCustomer);
            transaction.commit();
            return addedCustomer;
        } catch (Exception e) {
            transaction.rollback();
            log.error("Exception occurred at addOrUpdateCustomer() Method in Customer Dao ", e);
        } finally {
            if (session.isOpen()) session.close();
        }
        return null;
    }

    @Override
    public List<Customer> getAllCustomers(SearchDto searchDto) {
        log.info("getAllCustomers Method in Customer Dao");
        Session session = sessionFactory.openSession();
        List<Customer> customers = null;
        try {
            String hql = "FROM Customer c WHERE c.client = :client";
            Query query = session.createQuery(hql, Customer.class);
            query.setParameter("client", searchDto.getClient());
            query.setFirstResult(searchDto.getOffset() * searchDto.getLimit());
            query.setMaxResults(searchDto.getLimit());
            customers = query.getResultList();
        } catch (Exception e) {
            log.error("Exception occurred at getAllCustomers() in Customer Dao ", e);
        } finally {
            if (session.isOpen()) session.close();
        }
        return customers;
    }

    @Override
    public List<Customer> getCustomer(SearchDto searchDto) {
        try (Session session = sessionFactory.openSession()) {
            StringBuilder hql = new StringBuilder("FROM Customer WHERE client = :client");

            if (CustomerProductUtils.isNotNullAndEmpty(searchDto.getName())) {
                hql.append(" AND name = :name");
            }
            if (CustomerProductUtils.isNotNullAndEmpty(searchDto.getPhoneNo())) {
                hql.append(" AND phoneNo = :phoneNo");
            }
            if (CustomerProductUtils.isNotNullAndEmpty(searchDto.getCustomerCode())) {
                hql.append(" AND customerCode = :customerCode");
            }
            if (CustomerProductUtils.isNotNullAndEmpty(searchDto.getEmail())) {
                hql.append(" AND email = :email");
            }
            if (searchDto.getLastModifiedDate() != null) {
                hql.append(" AND lastModifiedDate = :lastModifiedDate");
            }
            if (searchDto.getCreate_date() != null) {
                hql.append(" AND create_date = :create_date");
            }
            if (searchDto.getId() > 0) {
                hql.append(" AND id = :id");
            }

            Query query = session.createQuery(hql.toString(), Customer.class);
            query.setParameter("client", searchDto.getClient());

            if (CustomerProductUtils.isNotNullAndEmpty(searchDto.getName())) {
                query.setParameter("name", searchDto.getName());
            }
            if (CustomerProductUtils.isNotNullAndEmpty(searchDto.getPhoneNo())) {
                query.setParameter("phoneNo", searchDto.getPhoneNo());
            }
            if (CustomerProductUtils.isNotNullAndEmpty(searchDto.getCustomerCode())) {
                query.setParameter("customerCode", searchDto.getCustomerCode());
            }
            if (CustomerProductUtils.isNotNullAndEmpty(searchDto.getEmail())) {
                query.setParameter("email", searchDto.getEmail());
            }
            if (searchDto.getLastModifiedDate() != null) {
                query.setParameter("lastModifiedDate", searchDto.getLastModifiedDate());
            }
            if (searchDto.getCreate_date() != null) {
                query.setParameter("create_date", searchDto.getCreate_date());
            }
            if (searchDto.getId() > 0) {
                query.setParameter("id", searchDto.getId());
            }

            List<Customer> customers = query.getResultList();
            log.info("List of customers are : {} ", customers);
            return customers;
        } catch (Exception e) {
            log.error("Exception occurred at getCustomers() in Customer Dao ", e);
            return null;
        }
    }
}
