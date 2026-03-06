package io.itookthese.api.service;

import io.itookthese.api.dto.CategoryRequest;
import io.itookthese.api.dto.CategoryResponse;
import io.itookthese.api.entity.Category;
import io.itookthese.api.repository.CategoryRepository;
import io.itookthese.api.repository.PhotoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;
  private final PhotoRepository photoRepository;

  @Transactional(readOnly = true)
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

  @Transactional(readOnly = true)
  public CategoryResponse getCategoryBySlug(String slug) {
    if (slug == null || slug.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Slug must not be blank");
    }
    Category category =
        categoryRepository
            .findBySlug(slug)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    return new CategoryResponse(
        category.getId(), category.getName(), category.getSlug(), category.getDescription());
  }

  @Transactional
  public CategoryResponse createCategory(CategoryRequest categoryRequest) {
    Category newCategory = mapCategoryRequestToCategory(categoryRequest);
    newCategory = categoryRepository.save(newCategory);
    return new CategoryResponse(
        newCategory.getId(),
        newCategory.getName(),
        newCategory.getSlug(),
        newCategory.getDescription());
  }

  @Transactional
  public CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest) {
    Category existingCategory =
        categoryRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    existingCategory.setName(categoryRequest.name());
    existingCategory.setSlug(categoryRequest.slug());
    existingCategory.setDescription(categoryRequest.description());
    existingCategory.setSortOrder(categoryRequest.sortOrder());
    Category updatedCategory = categoryRepository.save(existingCategory);
    return new CategoryResponse(
        updatedCategory.getId(),
        updatedCategory.getName(),
        updatedCategory.getSlug(),
        updatedCategory.getDescription());
  }

  @Transactional
  public void deleteCategory(Long id) {
    // Only delete a category if there are no photos with this category
    boolean exists = photoRepository.existsByCategoryId(id);
    if (exists) {
      throw new ResponseStatusException(HttpStatus.CONFLICT);
    }
    categoryRepository.deleteById(id);
  }

  private Category mapCategoryRequestToCategory(CategoryRequest categoryRequest) {
    return Category.builder()
        .name(categoryRequest.name())
        .description(categoryRequest.description())
        .slug(categoryRequest.slug())
        .sortOrder(categoryRequest.sortOrder())
        .build();
  }
}
