package io.itookthese.api.controller;

import io.itookthese.api.dto.CategoryRequest;
import io.itookthese.api.dto.CategoryResponse;
import io.itookthese.api.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

  private final CategoryService categoryService;

  @PostMapping
  public ResponseEntity<CategoryResponse> createCategory(
      @RequestBody @Valid CategoryRequest categoryRequest) {
    CategoryResponse categoryResponse = categoryService.createCategory(categoryRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(categoryResponse);
  }

  @PutMapping("/{id}")
  public ResponseEntity<CategoryResponse> updateCategory(
      @PathVariable Long id, @RequestBody @Valid CategoryRequest categoryRequest) {
    CategoryResponse categoryResponse = categoryService.updateCategory(id, categoryRequest);
    return ResponseEntity.ok(categoryResponse);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
    categoryService.deleteCategory(id);
    return ResponseEntity.noContent().build();
  }
}
