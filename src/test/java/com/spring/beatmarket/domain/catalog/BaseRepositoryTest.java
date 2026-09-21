package com.spring.beatmarket.domain.catalog;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Import(CatalogTestPersister.class)
abstract class BaseRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    protected TestEntityManager entityManager;

    @Autowired
    protected CatalogTestPersister persister;

    @BeforeEach
    void clearDatabase() {
        entityManager.getEntityManager().createQuery("DELETE FROM Song").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM Album").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM Artist").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM Genre").executeUpdate();
    }

    protected void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}