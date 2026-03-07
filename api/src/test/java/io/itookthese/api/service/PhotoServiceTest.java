package io.itookthese.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.itookthese.api.dto.ImageProcessingResult;
import io.itookthese.api.dto.PhotoDetailResponse;
import io.itookthese.api.dto.PhotoSummaryResponse;
import io.itookthese.api.dto.PhotoUpdateRequest;
import io.itookthese.api.entity.Category;
import io.itookthese.api.entity.Photo;
import io.itookthese.api.repository.CategoryRepository;
import io.itookthese.api.repository.PhotoRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class PhotoServiceTest {

  @Mock private PhotoRepository photoRepository;
  @Mock private ImageProcessingService imageProcessingService;
  @Mock private CategoryRepository categoryRepository;
  @Mock private MultipartFile multipartFile;

  @InjectMocks private PhotoService photoService;

  private Category sampleCategory() {
    return Category.builder()
        .id(1L)
        .name("Landscape")
        .slug("landscape")
        .description("Landscape photos")
        .sortOrder(0)
        .build();
  }

  private Photo samplePhoto() {
    return Photo.builder()
        .id(1L)
        .title("Sunset")
        .description("A beautiful sunset")
        .filenameThumb("thumb.jpg")
        .filenameMedium("medium.jpg")
        .filenameFull("full.jpg")
        .filenameOriginal("original.jpg")
        .placeholderBase64("base64data")
        .width(800)
        .height(600)
        .isFeatured(true)
        .sortOrder(0)
        .exifData("{\"camera\":\"iPhone 15\"}")
        .category(sampleCategory())
        .build();
  }

  private Photo samplePhotoNoCategory() {
    return Photo.builder()
        .id(2L)
        .title("Abstract")
        .description("Abstract art")
        .filenameThumb("thumb2.jpg")
        .filenameMedium("medium2.jpg")
        .filenameFull("full2.jpg")
        .filenameOriginal("original2.jpg")
        .width(1024)
        .height(768)
        .isFeatured(false)
        .sortOrder(1)
        .category(null)
        .build();
  }

  @SuppressWarnings("unchecked")
  @Test
  void getAllPhotos_returnsMappedList() {
    when(photoRepository.findAll(any(Specification.class), any(Sort.class)))
        .thenReturn(List.of(samplePhoto()));

    List<PhotoSummaryResponse> result = photoService.getAllPhotos(null, null);

    assertThat(result).hasSize(1);
    PhotoSummaryResponse response = result.get(0);
    assertThat(response.id()).isEqualTo(1L);
    assertThat(response.title()).isEqualTo("Sunset");
    assertThat(response.thumbUrl()).isEqualTo("/api/v1/images/thumb/thumb.jpg");
    assertThat(response.isFeatured()).isTrue();
    assertThat(response.category().name()).isEqualTo("Landscape");
  }

  @SuppressWarnings("unchecked")
  @Test
  void getAllPhotos_withNullCategory_returnsNullCategory() {
    when(photoRepository.findAll(any(Specification.class), any(Sort.class)))
        .thenReturn(List.of(samplePhotoNoCategory()));

    List<PhotoSummaryResponse> result = photoService.getAllPhotos(null, null);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).category()).isNull();
  }

  @SuppressWarnings("unchecked")
  @Test
  void getAllPhotos_emptyList_returnsEmptyList() {
    when(photoRepository.findAll(any(Specification.class), any(Sort.class)))
        .thenReturn(List.of());
    assertThat(photoService.getAllPhotos(null, null)).isEmpty();
  }

  @Test
  void getPhotoById_existingId_returnsDetail() {
    when(photoRepository.findById(1L)).thenReturn(Optional.of(samplePhoto()));

    PhotoDetailResponse result = photoService.getPhotoById(1L);

    assertThat(result.id()).isEqualTo(1L);
    assertThat(result.title()).isEqualTo("Sunset");
    assertThat(result.description()).isEqualTo("A beautiful sunset");
    assertThat(result.thumbUrl()).isEqualTo("/api/v1/images/thumb/thumb.jpg");
    assertThat(result.mediumUrl()).isEqualTo("/api/v1/images/medium/medium.jpg");
    assertThat(result.fullUrl()).isEqualTo("/api/v1/images/full/full.jpg");
    assertThat(result.exifData()).isEqualTo("{\"camera\":\"iPhone 15\"}");
    assertThat(result.category().slug()).isEqualTo("landscape");
  }

  @Test
  void getPhotoById_notFound_throwsException() {
    when(photoRepository.findById(999L)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> photoService.getPhotoById(999L))
        .isInstanceOf(ResponseStatusException.class);
  }

  @Test
  void uploadPhoto_savesAndReturnsDetail() {
    ImageProcessingResult processingResult =
        new ImageProcessingResult(
            "uuid_thumb.jpg", "uuid_medium.jpg", "uuid_full.jpg", "", "{}", 1920, 1080);
    when(multipartFile.getOriginalFilename()).thenReturn("photo.jpg");
    when(imageProcessingService.processImage(multipartFile)).thenReturn(processingResult);

    Photo savedPhoto =
        Photo.builder()
            .id(10L)
            .title("photo.jpg")
            .filenameThumb("uuid_thumb.jpg")
            .filenameMedium("uuid_medium.jpg")
            .filenameFull("uuid_full.jpg")
            .filenameOriginal("photo.jpg")
            .sortOrder(0)
            .isFeatured(false)
            .exifData("{}")
            .width(1920)
            .height(1080)
            .category(null)
            .build();
    when(photoRepository.save(any(Photo.class))).thenReturn(savedPhoto);

    PhotoDetailResponse result = photoService.uploadPhoto(multipartFile);

    assertThat(result.id()).isEqualTo(10L);
    assertThat(result.title()).isEqualTo("photo.jpg");
    assertThat(result.width()).isEqualTo(1920);
    assertThat(result.height()).isEqualTo(1080);
    verify(imageProcessingService).processImage(multipartFile);
    verify(photoRepository).save(any(Photo.class));
  }

  @Test
  void deletePhoto_existingId_deletesPhoto() {
    Photo photo = samplePhoto();
    when(photoRepository.findById(1L)).thenReturn(Optional.of(photo));
    ReflectionTestUtils.setField(photoService, "storagePath", System.getProperty("java.io.tmpdir"));

    photoService.deletePhoto(1L);

    verify(photoRepository).findById(1L);
    verify(photoRepository).deleteById(1L);
  }

  @Test
  void deletePhoto_notFound_throwsException() {
    when(photoRepository.findById(999L)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> photoService.deletePhoto(999L))
        .isInstanceOf(ResponseStatusException.class);
  }

  @Test
  void updatePhoto_existingPhoto_updatesAndReturns() {
    Photo existingPhoto = samplePhoto();
    Category newCategory =
        Category.builder().id(2L).name("Nature").slug("nature").description("Nature photos").build();
    PhotoUpdateRequest request = new PhotoUpdateRequest(2L, "Updated Title", "Updated desc", 5, false);

    when(photoRepository.findById(1L)).thenReturn(Optional.of(existingPhoto));
    when(categoryRepository.findById(2L)).thenReturn(Optional.of(newCategory));
    when(photoRepository.save(any(Photo.class))).thenAnswer(inv -> inv.getArgument(0));

    PhotoDetailResponse result = photoService.updatePhoto(1L, request);

    assertThat(result.title()).isEqualTo("Updated Title");
    assertThat(result.description()).isEqualTo("Updated desc");
    assertThat(result.isFeatured()).isFalse();
    assertThat(result.category().name()).isEqualTo("Nature");
  }

  @Test
  void updatePhoto_withNullCategoryId_setsNullCategory() {
    Photo existingPhoto = samplePhoto();
    PhotoUpdateRequest request = new PhotoUpdateRequest(null, "Title", "Desc", 0, true);

    when(photoRepository.findById(1L)).thenReturn(Optional.of(existingPhoto));
    when(photoRepository.save(any(Photo.class))).thenAnswer(inv -> inv.getArgument(0));

    PhotoDetailResponse result = photoService.updatePhoto(1L, request);
    assertThat(result.category()).isNull();
  }

  @Test
  void updatePhoto_photoNotFound_throwsException() {
    PhotoUpdateRequest request = new PhotoUpdateRequest(null, "Title", "Desc", 0, true);
    when(photoRepository.findById(999L)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> photoService.updatePhoto(999L, request))
        .isInstanceOf(ResponseStatusException.class);
  }

  @Test
  void updatePhoto_categoryNotFound_throwsException() {
    Photo existingPhoto = samplePhoto();
    PhotoUpdateRequest request = new PhotoUpdateRequest(999L, "Title", "Desc", 0, true);

    when(photoRepository.findById(1L)).thenReturn(Optional.of(existingPhoto));
    when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> photoService.updatePhoto(1L, request))
        .isInstanceOf(ResponseStatusException.class);
  }

  @Test
  void updatePhotoOrder_updatesEachPhotoSortOrder() {
    List<Long> photoIds = List.of(3L, 1L, 2L);
    photoService.updatePhotoOrder(photoIds);
    verify(photoRepository).updateSortOrderById(3L, 0);
    verify(photoRepository).updateSortOrderById(1L, 1);
    verify(photoRepository).updateSortOrderById(2L, 2);
  }

  @Test
  void updatePhotoOrder_emptyList_doesNothing() {
    photoService.updatePhotoOrder(List.of());
    verify(photoRepository, org.mockito.Mockito.never()).updateSortOrderById(any(), any());
  }
}
