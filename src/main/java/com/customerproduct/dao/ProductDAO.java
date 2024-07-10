package com.customerproduct.dao;

import com.customerproduct.dto.ProductSearchDTO;
import com.customerproduct.model.Customer;
import com.customerproduct.model.Product;
import com.customerproduct.repository.ProductRepository;
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
public class ProductDAO implements ProductRepository {


    @Qualifier("entityManagerFactory")
    private final SessionFactory sessionFactory;

    private final Logger log = LoggerFactory.getLogger(CustomerDao.class);


    @Override
    public Product addOrUpdateProduct(Product product) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            Product addedProduct=session.merge(product);
            session.flush();
            session.refresh(addedProduct);
            transaction.commit();
            return addedProduct;
        } catch (Exception e) {
            transaction.rollback();
            log.error("Exception occurred at addOrUpdateProduct() Method in Product Dao ", e);
        } finally {
            if (session.isOpen()) session.close();
        }
        return null;
    }

    @Override
    public List<Product> getAllProducts(ProductSearchDTO productSearchDTO) {
        log.info("getAllProducts Method in Product Dao");
        Session session = sessionFactory.openSession();
        List<Product> products = null;
        try {
            String hql = "FROM Product";
            Query query = session.createQuery(hql, Product.class);
            query.setFirstResult(productSearchDTO.getOffset() * productSearchDTO.getLimit());
            query.setMaxResults(productSearchDTO.getLimit());
            products = query.getResultList();
        } catch (Exception e) {
            log.error("Exception occurred at getAllProducts in Product Dao ", e);
        } finally {
            if (session.isOpen()) session.close();
        }
        return products;
    }

    @Override
    public List<Product> getProduct(ProductSearchDTO productSearchDTO) {
        try (Session session = sessionFactory.openSession()) {
            StringBuilder hql = new StringBuilder("FROM Product WHERE client_id= :client_id");

            if (CustomerProductUtils.isNotNullAndEmpty(productSearchDTO.getName())) {
                hql.append(" AND name = :name");
            }
            if (productSearchDTO.getEmp_id()>0) {
                hql.append(" AND emp_id = :emp_id");
            }
            if (CustomerProductUtils.isNotNullAndEmpty(productSearchDTO.getSkuCode())) {
                hql.append(" AND skuCode = :skuCode");
            }
            if (productSearchDTO.getLast_modified_date()!=null) {
                hql.append(" AND last_modified_date = :last_modified_date");
            }
            if (productSearchDTO.getId() > 0) {
                hql.append(" AND id = :id");
            }

            Query query = session.createQuery(hql.toString(), Product.class);
            query.setParameter("client_id",productSearchDTO.getClient_id());
            if(productSearchDTO.getId()>0) {
                query.setParameter("id", productSearchDTO.getId());
            }
            if (CustomerProductUtils.isNotNullAndEmpty(productSearchDTO.getName())) {
                query.setParameter("name", productSearchDTO.getName());
            }
            if (productSearchDTO.getEmp_id()>0) {
                query.setParameter("emp_id", productSearchDTO.getEmp_id());
            }
            if(CustomerProductUtils.isNotNullAndEmpty(productSearchDTO.getSkuCode())){
                query.setParameter("skuCode",productSearchDTO.getSkuCode());
            }
            if (productSearchDTO.getLast_modified_date()!= null) {
                query.setParameter("last_modified_date", productSearchDTO.getLast_modified_date());
            }

            List<Product> products = query.getResultList();
            log.info("List of products are : {} ", products);
            return products;
        } catch (Exception e) {
            log.error("Exception occurred at getProducts() in Product Dao ", e);
            return null;
        }
    }
}
