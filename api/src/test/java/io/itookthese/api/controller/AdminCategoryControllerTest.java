package io.itookthese.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.itookthese.api.config.GlobalExceptionHandler;
import io.itookthese.api.dto.CategoryRequest;
import io.itookthese.api.dto.CategoryResponse;
import io.itookthese.api.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AdminCategoryControllerTest {

  @Mock private CategoryService categoryService;
  @InjectMocks private AdminCategoryController adminCategoryController;
  private MockMvc mockMvc;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(adminCategoryController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  private CategoryResponse sampleCategory() {
    return new CategoryResponse(1L, "Landscape", "landscape", "Landscape photos");
  }

  @Test
  void createCategory_returns201() throws Exception {
    CategoryRequest request = new CategoryRequest("Landscape", "Landscape photos", "landscape", 1);
    when(categoryService.createCategory(any())).thenReturn(sampleCategory());

    mockMvc
        .perform(
            post("/api/v1/admin/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Landscape"))
        .andExpect(jsonPath("$.slug").value("landscape"));
  }

  @Test
  void updateCategory_returns200() throws Exception {
    CategoryRequest request = new CategoryRequest("Updated", "Updated desc", "updated", 2);
    CategoryResponse updated = new CategoryResponse(1L, "Updated", "updated", "Updated desc");
    when(categoryService.updateCategory(eq(1L), any())).thenReturn(updated);

    mockMvc
        .perform(
            put("/api/v1/admin/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated"))
        .andExpect(jsonPath("$.slug").value("updated"))
        .andExpect(jsonPath("$.description").value("Updated desc"));
  }

  @Test
  void updateCategory_notFound_returns404() throws Exception {
    CategoryRequest request = new CategoryRequest("Updated", "Updated desc", "updated", 2);
    when(categoryService.updateCategory(eq(999L), any()))
        .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    mockMvc
        .perform(
            put("/api/v1/admin/categories/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  void deleteCategory_returns204() throws Exception {
    doNothing().when(categoryService).deleteCategory(1L);

    mockMvc
        .perform(delete("/api/v1/admin/categories/1"))
        .andExpect(status().isNoContent());
  }

  @Test
  void deleteCategory_conflict_returns409() throws Exception {
    doThrow(new ResponseStatusException(HttpStatus.CONFLICT))
        .when(categoryService)
        .deleteCategory(1L);

    mockMvc
        .perform(delete("/api/v1/admin/categories/1"))
        .andExpect(status().isConflict());
  }
}
