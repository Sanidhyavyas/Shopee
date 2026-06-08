package com.shopee.shopee_backend;

import com.shopee.shopee_backend.dto.AuditLogDto;
import com.shopee.shopee_backend.dto.CreateOrderRequestDto;
import com.shopee.shopee_backend.dto.OrderItemRequestDto;
import com.shopee.shopee_backend.entity.AuditLog;
import com.shopee.shopee_backend.entity.Franchise;
import com.shopee.shopee_backend.entity.Product;
import com.shopee.shopee_backend.entity.User;
import com.shopee.shopee_backend.repository.AuditLogRepository;
import com.shopee.shopee_backend.repository.FranchiseRepository;
import com.shopee.shopee_backend.repository.ProductRepository;
import com.shopee.shopee_backend.repository.UserRepository;
import com.shopee.shopee_backend.service.AuditLogService;
import com.shopee.shopee_backend.service.OrderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Integration tests for AuditLogService against an H2 in-memory database.
 *
 * The inner {@link SyncAsyncConfig} replaces Spring's async executor with a
 * synchronous one so that {@code @Async} methods execute on the calling thread.
 * This makes audit-log persistence predictable inside {@code @Transactional}
 * test methods (everything rolls back cleanly after each test).
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuditLogServiceIntegrationTest {

    // ---------------------------------------------------------------
    // Make @Async methods run synchronously for deterministic tests
    // ---------------------------------------------------------------
    @TestConfiguration
    static class SyncAsyncConfig {
        @Bean
        AsyncConfigurer syncAsyncConfigurer() {
            return new AsyncConfigurer() {
                @Override
                public Executor getAsyncExecutor() {
                    // Run the submitted task immediately on the calling thread.
                    // This keeps all persistence inside the test transaction so
                    // changes are visible before the transaction rolls back.
                    return Runnable::run;
                }
            };
        }
    }

    // ---------------------------------------------------------------
    // Injected beans
    // ---------------------------------------------------------------
    @Autowired private AuditLogService    auditLogService;
    @Autowired private AuditLogRepository auditLogRepository;
    @Autowired private OrderService       orderService;
    @Autowired private ProductRepository  productRepository;
    @Autowired private FranchiseRepository franchiseRepository;
    @Autowired private UserRepository     userRepository;
    @Autowired private PasswordEncoder    passwordEncoder;

    // ---------------------------------------------------------------
    // Test fixtures (created fresh for each test; rolled back after)
    // ---------------------------------------------------------------
    private User     testUser;
    private Franchise testFranchise;
    private Product   testProduct;

    @BeforeEach
    void setUp() {
        // 1. Persist a FRANCHISE_ADMIN user
        testUser = new User();
        testUser.setName("Integration Tester");
        testUser.setEmail("integ-test@shopee.com");
        testUser.setMobile("9000000099");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setRole("FRANCHISE_ADMIN");
        testUser.setActive(true);
        testUser = userRepository.save(testUser);

        // 2. Persist a franchise owned by that user
        testFranchise = new Franchise();
        testFranchise.setOutletName("Test Outlet");
        testFranchise.setOwner(testUser);
        testFranchise.setAddress("1 Test Street");
        testFranchise.setCity("Testville");
        testFranchise.setState("TS");
        testFranchise.setValidFrom(LocalDate.now());
        testFranchise.setValidTo(LocalDate.now().plusYears(1));
        testFranchise = franchiseRepository.save(testFranchise);

        // 3. Persist a product belonging to that franchise
        testProduct = new Product();
        testProduct.setName("Widget");
        testProduct.setSku("SKU-INTEG-001");
        testProduct.setPrice(new BigDecimal("50.00"));
        testProduct.setCostPrice(new BigDecimal("30.00"));
        testProduct.setStockQuantity(100);
        testProduct.setMinStockAlert(5);
        testProduct.setFranchise(testFranchise);
        testProduct.setActive(true);
        testProduct = productRepository.save(testProduct);

        // 4. Put the user in the security context (simulates an authenticated request)
        var auth = new UsernamePasswordAuthenticationToken(
                testUser.getEmail(), null,
                List.of(new SimpleGrantedAuthority("ROLE_FRANCHISE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        // Clear the security context so it does not bleed into other tests
        SecurityContextHolder.clearContext();
    }

    // ===============================================================
    // Test 1 — log() is persisted after createOrder
    // ===============================================================
    @Test
    @DisplayName("An audit log entry with action CREATE_ORDER is written after createOrder()")
    void logIsPersistedAfterCreateOrder() {
        // Arrange
        CreateOrderRequestDto request = new CreateOrderRequestDto();
        request.setCustomerName("Bob");
        request.setCustomerMobile("9111111111");
        request.setItems(List.of(new OrderItemRequestDto(testProduct.getProductId(), 2)));

        // Act
        orderService.createOrder(testFranchise.getFranchiseId(), request);

        // Assert: exactly one CREATE_ORDER audit entry exists
        List<AuditLog> logs = auditLogRepository.findAll();
        assertThat(logs)
                .filteredOn(l -> "CREATE_ORDER".equals(l.getAction()))
                .as("Expected one CREATE_ORDER audit log entry")
                .hasSize(1);

        AuditLog entry = logs.stream()
                .filter(l -> "CREATE_ORDER".equals(l.getAction()))
                .findFirst()
                .orElseThrow();

        assertThat(entry.getEntityType()).isEqualTo("Order");
        assertThat(entry.getActorEmail()).isEqualTo(testUser.getEmail());
        assertThat(entry.getActorRole()).isEqualTo("FRANCHISE_ADMIN");
        assertThat(entry.getOldValue()).isNull();
        assertThat(entry.getNewValue()).isNotBlank();
        assertThat(entry.getTimestamp()).isNotNull();
    }

    // ===============================================================
    // Test 2 — logs are retrievable by entityId
    // ===============================================================
    @Test
    @DisplayName("getLogsForEntity() returns the correct audit entry for a given entityId")
    void logsAreRetrievableByEntityId() {
        // Arrange: write two entries for different entities
        auditLogService.log("UPDATE_PRODUCT", "Product", "999",
                "{\"name\":\"Old Widget\"}", "{\"name\":\"New Widget\"}");
        auditLogService.log("DELETE_PRODUCT", "Product", "888",
                "{\"name\":\"Gone\"}", null);

        // Act: query only for entity 999
        Page<AuditLogDto> page =
                auditLogService.getLogsForEntity("Product", "999", Pageable.unpaged());

        // Assert
        assertThat(page.getTotalElements()).isEqualTo(1);
        AuditLogDto dto = page.getContent().get(0);
        assertThat(dto.getEntityId()).isEqualTo("999");
        assertThat(dto.getEntityType()).isEqualTo("Product");
        assertThat(dto.getAction()).isEqualTo("UPDATE_PRODUCT");
        assertThat(dto.getActorEmail()).isEqualTo(testUser.getEmail());
        assertThat(dto.getOldValue()).contains("Old Widget");
        assertThat(dto.getNewValue()).contains("New Widget");
    }

    // ===============================================================
    // Test 3 — log() does not throw when SecurityContext is empty
    // ===============================================================
    @Test
    @DisplayName("log() silently no-ops (no exception, no DB row) when there is no authenticated user")
    void logDoesNotThrowWhenSecurityContextIsEmpty() {
        // Arrange: remove the authentication set up in @BeforeEach
        SecurityContextHolder.clearContext();

        // Act + Assert: must not throw
        assertThatCode(() ->
                auditLogService.log("CREATE_ORDER", "Order", "123", null, null))
                .doesNotThrowAnyException();

        // Assert: nothing was written to the database
        long count = auditLogRepository.findAll().stream()
                .filter(l -> "123".equals(l.getEntityId()))
                .count();
        assertThat(count).isZero();
    }
}
