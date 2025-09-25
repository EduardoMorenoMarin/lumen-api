package com.librerialumen.api;

import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base class for integration tests. Loads the full application context with the {@code test} profile active
 * and keeps the security stack intact (no JWT mocks).
 */
@SpringBootTest
@ActiveTestProfile
public abstract class AbstractIntegrationTest {
}

