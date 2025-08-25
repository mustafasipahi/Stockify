package com.stockify.project.initialization;

import com.stockify.project.model.entity.UserEntity;
import com.stockify.project.repository.UserRepository;
import com.stockify.project.tenant.TenantContext;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.stockify.project.constant.LoginConstant.*;
import static com.stockify.project.tenant.TenantType.*;

@Service
@RequiredArgsConstructor
public class DataInitializerService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initData() {
        createPublic();
        createGurme();
    }

    private void createPublic() {
        jdbcTemplate.execute("SET search_path TO " + PUBLIC.getStokifySchemaName());
        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS " + PUBLIC.getStokifySchemaName());
        TenantContext.setCurrentTenant(PUBLIC.getStokifySchemaName());
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS users (created_date TIMESTAMP(6), id BIGSERIAL PRIMARY KEY, password VARCHAR(255), stokify_schema_name VARCHAR(255), username VARCHAR(255));");
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
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS revinfo (rev INTEGER NOT NULL PRIMARY KEY, revtstmp BIGINT)");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS product (category_id BIGINT NOT NULL, created_date TIMESTAMP(6), id BIGSERIAL PRIMARY KEY, last_modified_date TIMESTAMP(6), name VARCHAR(255) NOT NULL, status VARCHAR(255) NOT NULL CONSTRAINT product_status_check CHECK ((status)::text = ANY ((ARRAY ['ACTIVE'::character varying, 'PASSIVE'::character varying])::text[])), stock_code VARCHAR(255) NOT NULL)");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS product_audit (rev INTEGER NOT NULL CONSTRAINT fk4tbqxvexjy6g6pybwhmok5jty REFERENCES revinfo, revtype SMALLINT, category_id BIGINT, created_date TIMESTAMP(6), id BIGINT NOT NULL, last_modified_date TIMESTAMP(6), name VARCHAR(255), status VARCHAR(255) CONSTRAINT product_audit_status_check CHECK ((status)::text = ANY ((ARRAY ['ACTIVE'::character varying, 'PASSIVE'::character varying])::text[])), stock_code VARCHAR(255), PRIMARY KEY (rev, id))");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS category (kdv DOUBLE PRECISION NOT NULL, created_date TIMESTAMP(6), id BIGSERIAL PRIMARY KEY, product_id BIGINT UNIQUE CONSTRAINT fkqqm689b1x9dotoq6okskaxnx4 REFERENCES product, name VARCHAR(255) NOT NULL)");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS inventory (critical_product_count INTEGER, price NUMERIC(38,2) NOT NULL, product_count INTEGER NOT NULL, created_date TIMESTAMP(6), id BIGSERIAL PRIMARY KEY, last_modified_date TIMESTAMP(6), product_id BIGINT UNIQUE CONSTRAINT fkp7gj4l80fx8v0uap3b2crjwp5 REFERENCES product, status VARCHAR(255) NOT NULL CONSTRAINT inventory_status_check CHECK ((status)::text = ANY ((ARRAY ['AVAILABLE'::character varying, 'CRITICAL'::character varying, 'OUT_OF_STOCK'::character varying])::text[])))");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS inventory_audit (critical_product_count INTEGER, price NUMERIC(38,2), product_count INTEGER, rev INTEGER NOT NULL CONSTRAINT fk3h626evubhd57u643mbgnguh4 REFERENCES revinfo, revtype SMALLINT, created_date TIMESTAMP(6), id BIGINT NOT NULL, last_modified_date TIMESTAMP(6), status VARCHAR(255) CONSTRAINT inventory_audit_status_check CHECK ((status)::text = ANY ((ARRAY ['AVAILABLE'::character varying, 'CRITICAL'::character varying, 'OUT_OF_STOCK'::character varying])::text[])), PRIMARY KEY (rev, id))");
        TenantContext.clear();
    }
}
