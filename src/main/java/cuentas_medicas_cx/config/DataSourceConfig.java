package cuentas_medicas_cx.config;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@Slf4j
public class DataSourceConfig {

    @Value("${spring.datasource.primary.jdbc-url}")
    private String primaryUrl;

    @Value("${spring.datasource.primary.username}")
    private String primaryUser;

    @Value("${spring.datasource.primary.password}")
    private String primaryPassword;

    @Value("${spring.datasource.external.jdbc-url}")
    private String externalUrl;

    @Value("${spring.datasource.external.username}")
    private String externalUser;

    @Value("${spring.datasource.external.password}")
    private String externalPassword;

    @Bean
    @Primary
    public DataSource primaryDataSource() {
        log.info("Iniciando PostgreSQL datasource: {}", primaryUrl);
        return DataSourceBuilder.create()
                .url(primaryUrl)
                .username(primaryUser)
                .password(primaryPassword)
                .driverClassName("org.postgresql.Driver")
                .build();
    }

    @Primary
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("primaryDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .properties(jpaProperties())
                .packages("cuentas_medicas_cx.model.entity")
                .persistenceUnit("default")
                .build();
    }

    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory.getObject());
    }

    private Map<String, Object> jpaProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.put("hibernate.hbm2ddl.auto", "update");
        props.put("hibernate.show_sql", true);
        props.put("hibernate.format_sql", true);
        return props;
    }

    @Bean
    public DataSource externalDataSource() {
        log.info("Iniciando SQL Server Dinámica datasource: jdbc:sqlserver://vulcano:1433;databaseName=DGEMPRES50");
        return DataSourceBuilder.create()
                .url(externalUrl)
                .username(externalUser)
                .password(externalPassword)
                .driverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver")
                .build();
    }

    @Bean
    public JdbcTemplate primaryJdbcTemplate(DataSource primaryDataSource) {
        return new JdbcTemplate(primaryDataSource);
    }

    @Bean
    public JdbcTemplate externalJdbcTemplate(DataSource externalDataSource) {
        return new JdbcTemplate(externalDataSource);
    }
}