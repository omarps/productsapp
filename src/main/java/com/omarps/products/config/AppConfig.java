package com.omarps.products.config;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import javax.sql.DataSource;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Controller;

/**
 * the repository objects app config.
 * @author omarps@gmail.com
 */
@Configuration
@EnableJpaRepositories(basePackages = {"com.omarps.products.repository"})
@ComponentScan(basePackages = {"com.omarps.products"}, excludeFilters = {
    @ComponentScan.Filter(value = Controller.class, type = FilterType.ANNOTATION),
    @ComponentScan.Filter(value = Configuration.class, type = FilterType.ANNOTATION)
})
public class AppConfig extends RepositoryRestMvcConfiguration {

    /**
     * configure the repository rest configuration.
     * @param config the rest config
     */
    @Override
    protected void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        super.configureRepositoryRestConfiguration(config);
        try {
            config.setBaseUri(new URI("/api"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * data source bean.
     * @return the data source
     */
    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()//
                .setType(EmbeddedDatabaseType.HSQL)//
                .build();
    }

    /**
     * jpa vendor adapter bean.
     * @return the jpa vendor adapter
     */
    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setShowSql(true);
        adapter.setGenerateDdl(true);
        adapter.setDatabase(Database.HSQL);
        return adapter;
    }

    /**
     * entity manager factory bean
     * @return the entity manager factory
     * @throws ClassNotFoundException 
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory()//
            throws ClassNotFoundException {
        LocalContainerEntityManagerFactoryBean factoryBean =//
                new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSource());
        factoryBean.setPackagesToScan("com.omarps.products.model");
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter());
        factoryBean.setJpaProperties(jpaProperties());

        return factoryBean;
    }

    /**
     * transaction manager bean.
     * @return the transaction manager
     * @throws ClassNotFoundException 
     */
    @Bean
    public JpaTransactionManager transactionManager()//
            throws ClassNotFoundException {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());

        return transactionManager;
    }

    /**
     * jpa properties bean.
     * @return the jpa properties
     */
    @Bean
    public Properties jpaProperties() {
        Properties properties = new Properties();
        properties.put(AvailableSettings.HBM2DDL_AUTO, "create-drop");
        return properties;
    }

}
