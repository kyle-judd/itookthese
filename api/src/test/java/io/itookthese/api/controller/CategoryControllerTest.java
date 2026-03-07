package io.itookthese.api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.itookthese.api.config.GlobalExceptionHandler;
import io.itookthese.api.dto.CategoryResponse;
import io.itookthese.api.service.CategoryService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

  @Mock private CategoryService categoryService;
  @InjectMocks private CategoryController categoryController;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(categoryController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  void getAllCategories_returnsList() throws Exception {
    CategoryResponse cat1 = new CategoryResponse(1L, "Landscape", "landscape", "Landscape photos");
    CategoryResponse cat2 = new CategoryResponse(2L, "Portrait", "portrait", "Portrait photos");
    when(categoryService.getAllCategories()).thenReturn(List.of(cat1, cat2));

    mockMvc
        .perform(get("/api/v1/categories"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].name").value("Landscape"))
        .andExpect(jsonPath("$[0].slug").value("landscape"))
        .andExpect(jsonPath("$[1].id").value(2))
        .andExpect(jsonPath("$[1].name").value("Portrait"));
  }

  @Test
  void getAllCategories_emptyList_returnsEmptyArray() throws Exception {
    when(categoryService.getAllCategories()).thenReturn(List.of());

    mockMvc
        .perform(get("/api/v1/categories"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").isEmpty());
  }

  @Test
  void getCategoryBySlug_returnsCategory() throws Exception {
    CategoryResponse cat = new CategoryResponse(1L, "Landscape", "landscape", "Landscape photos");
    when(categoryService.getCategoryBySlug("landscape")).thenReturn(cat);

    mockMvc
        .perform(get("/api/v1/categories/landscape"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Landscape"))
        .andExpect(jsonPath("$.slug").value("landscape"))
        .andExpect(jsonPath("$.description").value("Landscape photos"));
  }

  @Test
  void getCategoryBySlug_notFound_returns404() throws Exception {
    when(categoryService.getCategoryBySlug("nonexistent"))
        .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    mockMvc.perform(get("/api/v1/categories/nonexistent")).andExpect(status().isNotFound());
  }
}
