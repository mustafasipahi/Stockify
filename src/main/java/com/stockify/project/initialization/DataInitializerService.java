package com.stockify.project.initialization;

import com.stockify.project.model.entity.UserEntity;
import com.stockify.project.repository.UserRepository;
import com.stockify.project.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.stockify.project.constant.LoginConstant.*;
import static com.stockify.project.tenant.TenantType.*;

@Component
@RequiredArgsConstructor
public class DataInitializerService implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        createPublic();
        createGurme();
    }

    private void createPublic() {
        jdbcTemplate.execute("SET search_path TO " + PUBLIC.getStokifySchemaName());
        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS " + PUBLIC.getStokifySchemaName());
        TenantContext.setCurrentTenant(PUBLIC.getStokifySchemaName());
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS users (id BIGSERIAL PRIMARY KEY, username VARCHAR(255), password VARCHAR(255), stokify_schema_name VARCHAR(255), created_date TIMESTAMP(6));");
        if (userRepository.findByUsername(GURME_ADMIN_USER_NAME_1).isEmpty()) {
            UserEntity user = new UserEntity();
            user.setUsername(GURME_ADMIN_USER_NAME_1);
            user.setPassword(passwordEncoder.encode(GURME_ADMIN_USER_PASSWORD_1));
            user.setStokifySchemaName(GURME.getStokifySchemaName());
            userRepository.save(user);
        }
        if (userRepository.findByUsername(GURME_ADMIN_USER_NAME_2).isEmpty()) {
            UserEntity user = new UserEntity();
            user.setUsername(GURME_ADMIN_USER_NAME_2);
            user.setPassword(passwordEncoder.encode(GURME_ADMIN_USER_PASSWORD_2));
            user.setStokifySchemaName(GURME.getStokifySchemaName());
            userRepository.save(user);
        }
        TenantContext.clear();
    }

    private void createGurme() {
        jdbcTemplate.execute("SET search_path TO " + GURME.getStokifySchemaName());
        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS " + GURME.getStokifySchemaName());
        TenantContext.setCurrentTenant(GURME.getStokifySchemaName());
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS revinfo (rev INTEGER NOT NULL PRIMARY KEY, revtstmp BIGINT);");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS users (id BIGSERIAL PRIMARY KEY, username VARCHAR(255) NOT NULL, password VARCHAR(255) NOT NULL, stokify_schema_name VARCHAR(255) NOT NULL, created_date TIMESTAMP(6));");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS category (id BIGSERIAL PRIMARY KEY, name VARCHAR(255) NOT NULL, kdv DOUBLE PRECISION NOT NULL, created_date TIMESTAMP(6));");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS product (id BIGSERIAL PRIMARY KEY, category_id BIGINT NOT NULL REFERENCES category(id), stock_code VARCHAR(255) NOT NULL, name VARCHAR(255) NOT NULL, status VARCHAR(255) NOT NULL CONSTRAINT product_status_check CHECK (status IN ('ACTIVE','PASSIVE')), created_date TIMESTAMP(6), last_modified_date TIMESTAMP(6));");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS product_audit (id BIGINT NOT NULL, rev INTEGER NOT NULL REFERENCES revinfo(rev), revtype SMALLINT, category_id BIGINT, stock_code VARCHAR(255), name VARCHAR(255), status VARCHAR(255) CONSTRAINT product_audit_status_check CHECK (status IN ('ACTIVE','PASSIVE')), created_date TIMESTAMP(6), last_modified_date TIMESTAMP(6), PRIMARY KEY (rev, id));");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS inventory (id BIGSERIAL PRIMARY KEY, product_id BIGINT NOT NULL UNIQUE REFERENCES product(id), price NUMERIC(38,2) NOT NULL, product_count INTEGER NOT NULL, critical_product_count INTEGER, status VARCHAR(255) NOT NULL CONSTRAINT inventory_status_check CHECK (status IN ('AVAILABLE','CRITICAL','OUT_OF_STOCK')), created_date TIMESTAMP(6), last_modified_date TIMESTAMP(6));");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS inventory_audit (id BIGINT NOT NULL, rev INTEGER NOT NULL REFERENCES revinfo(rev), revtype SMALLINT, product_count INTEGER, critical_product_count INTEGER, price NUMERIC(38,2), status VARCHAR(255) CONSTRAINT inventory_audit_status_check CHECK (status IN ('AVAILABLE','CRITICAL','OUT_OF_STOCK')), created_date TIMESTAMP(6), last_modified_date TIMESTAMP(6), PRIMARY KEY (rev, id));");
        TenantContext.clear();
    }
}
