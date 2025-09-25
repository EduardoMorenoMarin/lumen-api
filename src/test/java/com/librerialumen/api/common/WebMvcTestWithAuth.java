package com.librerialumen.api.common;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

/**
 * Convenience meta-annotation for MVC slice tests. It wires a Mockito-based {@link MockJwtFilterConfiguration}
 * so that controllers can be exercised without passing through real JWT authentication. Combine with
 * {@code @WithMockUser} on individual test methods to simulate authenticated roles.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
@Import(MockJwtFilterConfiguration.class)
public @interface WebMvcTestWithAuth {

  @AliasFor(annotation = WebMvcTest.class, attribute = "controllers")
  Class<?>[] controllers() default {};
}
