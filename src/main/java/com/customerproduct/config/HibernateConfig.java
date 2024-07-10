package com.customerproduct.config;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class HibernateConfig {

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${spring.jpa.properties.hibernate.dialect}")
    private String HIBERNATE_DIALECT;


    @Value("${spring.datasource.url}")
    private String DATABASE_URL;

    @Value("${spring.datasource.username}")
    private String DATABASE_USERNAME;

    @Value("${spring.datasource.password}")
    private String DATABASE_PASSWORD;

    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create().url(DATABASE_URL).username(DATABASE_USERNAME).password(DATABASE_PASSWORD).build();
    }

    @Bean(name = "entityManagerFactory")
    public LocalSessionFactoryBean sessionFactory(@Qualifier("dataSource") DataSource dataSource) {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setPackagesToScan("com.customerproduct.*");
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
    }

    @Bean
    public PlatformTransactionManager hibernateTransactionManager(SessionFactory sessionFactory) {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory);
        return transactionManager;
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", HIBERNATE_DIALECT);
        return properties;
    }

}
