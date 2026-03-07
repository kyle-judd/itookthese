package io.itookthese.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.itookthese.api.dto.CategoryRequest;
import io.itookthese.api.dto.CategoryResponse;
import io.itookthese.api.entity.Category;
import io.itookthese.api.repository.CategoryRepository;
import io.itookthese.api.repository.PhotoRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

  @Mock private CategoryRepository categoryRepository;
  @Mock private PhotoRepository photoRepository;

  @InjectMocks private CategoryService categoryService;

  private Category sampleCategory() {
    return Category.builder()
        .id(1L)
        .name("Landscape")
        .slug("landscape")
        .description("Landscape photos")
        .sortOrder(0)
        .build();
  }

  @Test
  void getAllCategories_returnsMappedList() {
    Category cat1 = sampleCategory();
    Category cat2 =
        Category.builder()
            .id(2L)
            .name("Portrait")
            .slug("portrait")
            .description("Portrait photos")
            .sortOrder(1)
            .build();
    when(categoryRepository.findAll()).thenReturn(List.of(cat1, cat2));

    List<CategoryResponse> result = categoryService.getAllCategories();

    assertThat(result).hasSize(2);
    assertThat(result.get(0).name()).isEqualTo("Landscape");
    assertThat(result.get(0).slug()).isEqualTo("landscape");
    assertThat(result.get(1).name()).isEqualTo("Portrait");
  }

  @Test
  void getAllCategories_emptyList_returnsEmpty() {
    when(categoryRepository.findAll()).thenReturn(List.of());
    assertThat(categoryService.getAllCategories()).isEmpty();
  }

  @Test
  void getCategoryBySlug_existingSlug_returnsCategory() {
    when(categoryRepository.findBySlug("landscape")).thenReturn(Optional.of(sampleCategory()));

    CategoryResponse result = categoryService.getCategoryBySlug("landscape");

    assertThat(result.id()).isEqualTo(1L);
    assertThat(result.name()).isEqualTo("Landscape");
    assertThat(result.slug()).isEqualTo("landscape");
    assertThat(result.description()).isEqualTo("Landscape photos");
  }

  @Test
  void getCategoryBySlug_notFound_throwsException() {
    when(categoryRepository.findBySlug("nonexistent")).thenReturn(Optional.empty());
    assertThatThrownBy(() -> categoryService.getCategoryBySlug("nonexistent"))
        .isInstanceOf(ResponseStatusException.class);
  }

  @Test
  void getCategoryBySlug_nullSlug_throwsBadRequest() {
    assertThatThrownBy(() -> categoryService.getCategoryBySlug(null))
        .isInstanceOf(ResponseStatusException.class);
  }

  @Test
  void getCategoryBySlug_blankSlug_throwsBadRequest() {
    assertThatThrownBy(() -> categoryService.getCategoryBySlug("   "))
        .isInstanceOf(ResponseStatusException.class);
  }

  @Test
  void createCategory_savesAndReturnsResponse() {
    CategoryRequest request = new CategoryRequest("Wildlife", "Wildlife photos", "wildlife", 2);
    Category saved =
        Category.builder()
            .id(3L)
            .name("Wildlife")
            .slug("wildlife")
            .description("Wildlife photos")
            .sortOrder(2)
            .build();
    when(categoryRepository.save(any(Category.class))).thenReturn(saved);

    CategoryResponse result = categoryService.createCategory(request);

    assertThat(result.id()).isEqualTo(3L);
    assertThat(result.name()).isEqualTo("Wildlife");
    assertThat(result.slug()).isEqualTo("wildlife");
    verify(categoryRepository).save(any(Category.class));
  }

  @Test
  void updateCategory_existingId_updatesAndReturns() {
    Category existing = sampleCategory();
    CategoryRequest request = new CategoryRequest("Updated Name", "Updated desc", "updated-slug", 5);

    when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

    CategoryResponse result = categoryService.updateCategory(1L, request);

    assertThat(result.name()).isEqualTo("Updated Name");
    assertThat(result.slug()).isEqualTo("updated-slug");
    assertThat(result.description()).isEqualTo("Updated desc");
  }

  @Test
  void updateCategory_notFound_throwsException() {
    CategoryRequest request = new CategoryRequest("Name", "Desc", "slug", 0);
    when(categoryRepository.findById(999L)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> categoryService.updateCategory(999L, request))
        .isInstanceOf(ResponseStatusException.class);
  }

  @Test
  void deleteCategory_noPhotosAssociated_deletes() {
    when(photoRepository.existsByCategoryId(1L)).thenReturn(false);
    categoryService.deleteCategory(1L);
    verify(categoryRepository).deleteById(1L);
  }

  @Test
  void deleteCategory_hasPhotosAssociated_throwsConflict() {
    when(photoRepository.existsByCategoryId(1L)).thenReturn(true);
    assertThatThrownBy(() -> categoryService.deleteCategory(1L))
        .isInstanceOf(ResponseStatusException.class);
  }
}
