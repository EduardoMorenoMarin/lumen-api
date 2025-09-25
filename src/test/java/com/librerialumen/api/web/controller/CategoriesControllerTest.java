package com.librerialumen.api.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.librerialumen.api.common.WebMvcTestWithAuth;
import com.librerialumen.api.service.CategoryService;
import com.librerialumen.api.web.dto.category.CategoryCreateDTO;
import com.librerialumen.api.web.dto.category.CategoryUpdateDTO;
import com.librerialumen.api.web.dto.category.CategoryViewDTO;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTestWithAuth(controllers = CategoriesController.class)
class CategoriesControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private CategoryService categoryService;

  @Test
  @WithMockUser(username = "admin@lumen.test", roles = {"ADMIN"})
  @DisplayName("POST /api/v1/categories creates category")
  void createCategory_shouldReturnCategory() throws Exception {
    CategoryCreateDTO request = CategoryCreateDTO.builder()
        .name("Fiction")
        .description("Books and novels")
        .active(true)
        .build();

    CategoryViewDTO response = CategoryViewDTO.builder()
        .id(UUID.randomUUID())
        .name("Fiction")
        .description("Books and novels")
        .active(true)
        .createdAt(Instant.parse("2025-01-01T00:00:00Z"))
        .build();

    when(categoryService.create(any(CategoryCreateDTO.class))).thenReturn(response);

    mockMvc.perform(post("/api/v1/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(response.getId().toString()))
        .andExpect(jsonPath("$.name").value("Fiction"));
  }

  @Test
  @WithMockUser(username = "employee@lumen.test", roles = {"EMPLOYEE"})
  @DisplayName("POST /api/v1/categories returns 400 on invalid payload")
  void createCategory_shouldReturnBadRequest() throws Exception {
    String invalidPayload = "{\"name\":\"Fiction\",\"active\":\"invalid\"}";

    mockMvc.perform(post("/api/v1/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidPayload))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400));

    verify(categoryService, never()).create(any(CategoryCreateDTO.class));
  }

  @Test
  @WithMockUser(username = "employee@lumen.test", roles = {"EMPLOYEE"})
  @DisplayName("PUT /api/v1/categories/{id} updates category")
  void updateCategory_shouldReturnUpdatedCategory() throws Exception {
    UUID categoryId = UUID.randomUUID();
    CategoryUpdateDTO request = CategoryUpdateDTO.builder()
        .name("Sci-Fi")
        .description("Updated description")
        .active(false)
        .build();

    CategoryViewDTO response = CategoryViewDTO.builder()
        .id(categoryId)
        .name("Sci-Fi")
        .description("Updated description")
        .active(false)
        .updatedAt(Instant.parse("2025-01-02T00:00:00Z"))
        .build();

    when(categoryService.update(eq(categoryId), any(CategoryUpdateDTO.class))).thenReturn(response);

    mockMvc.perform(put("/api/v1/categories/{id}", categoryId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(categoryId.toString()))
        .andExpect(jsonPath("$.active").value(false));

    verify(categoryService).update(eq(categoryId), any(CategoryUpdateDTO.class));
  }

  @Test
  @WithMockUser(username = "employee@lumen.test", roles = {"EMPLOYEE"})
  @DisplayName("GET /api/v1/categories returns list")
  void listCategories_shouldReturnList() throws Exception {
    CategoryViewDTO category = CategoryViewDTO.builder()
        .id(UUID.randomUUID())
        .name("Non Fiction")
        .active(true)
        .build();
    when(categoryService.list()).thenReturn(List.of(category));

    mockMvc.perform(get("/api/v1/categories"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Non Fiction"));
  }

  @Test
  @WithMockUser(username = "employee@lumen.test", roles = {"EMPLOYEE"})
  @DisplayName("GET /api/v1/categories/{id} returns detail")
  void getCategory_shouldReturnDetail() throws Exception {
    UUID categoryId = UUID.randomUUID();
    CategoryViewDTO category = CategoryViewDTO.builder()
        .id(categoryId)
        .name("Sci-Fi")
        .active(true)
        .build();
    when(categoryService.get(categoryId)).thenReturn(category);

    mockMvc.perform(get("/api/v1/categories/{id}", categoryId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(categoryId.toString()))
        .andExpect(jsonPath("$.name").value("Sci-Fi"));
  }
}
