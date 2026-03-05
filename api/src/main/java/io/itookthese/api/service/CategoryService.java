package io.itookthese.api.service;

import io.itookthese.api.dto.CategoryResponse;
import io.itookthese.api.entity.Category;
import io.itookthese.api.repository.CategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;

  public List<CategoryResponse> getAllCategories() {
    return categoryRepository.findAll().stream()
        .map(
            category ->
                new CategoryResponse(
                    category.getId(),
                    category.getName(),
                    category.getSlug(),
                    category.getDescription()))
        .toList();
  }

  public CategoryResponse getCategoryBySlug(String slug) {
    Category category =
        categoryRepository
            .findBySlug(slug)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    return new CategoryResponse(
        category.getId(), category.getName(), category.getSlug(), category.getDescription());
  }
}
