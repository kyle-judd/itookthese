package io.itookthese.api.controller;

import io.itookthese.api.dto.CategoryResponse;
import io.itookthese.api.service.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;

  @GetMapping
  public List<CategoryResponse> getAllCategories() {
    return categoryService.getAllCategories();
  }

  @GetMapping("/{slug}")
  public CategoryResponse getCategoryBySlug(@PathVariable String slug) {
    return categoryService.getCategoryBySlug(slug);
  }
}
