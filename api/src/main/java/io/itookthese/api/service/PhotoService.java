package io.itookthese.api.service;

import io.itookthese.api.dto.*;
import io.itookthese.api.entity.Category;
import io.itookthese.api.entity.Photo;
import io.itookthese.api.repository.CategoryRepository;
import io.itookthese.api.repository.PhotoRepository;
import io.itookthese.api.specification.PhotoSpecification;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoService {
  private static final String THUMB_URL_PREFIX = "/api/v1/images/thumb/";
  private static final String MEDIUM_URL_PREFIX = "/api/v1/images/medium/";
  private static final String FULL_URL_PREFIX = "/api/v1/images/full/";

  private final PhotoRepository photoRepository;
  private final ImageProcessingService imageProcessingService;
  private final CategoryRepository categoryRepository;

  @Value("${storage.path}")
  private String storagePath;

  @Transactional(readOnly = true)
  public List<PhotoSummaryResponse> getAllPhotos(Long categoryId, Boolean isFeatured) {
    return photoRepository
        .findAll(
            PhotoSpecification.withFilters(categoryId, isFeatured),
            Sort.by(Sort.Direction.ASC, "sortOrder"))
        .stream()
        .map(
            photo ->
                new PhotoSummaryResponse(
                    photo.getId(),
                    photo.getTitle(),
                    THUMB_URL_PREFIX + photo.getFilenameThumb(),
                    photo.getPlaceholderBase64(),
                    photo.getWidth(),
                    photo.getHeight(),
                    photo.getIsFeatured(),
                    mapCategory(photo)))
        .toList();
  }

  @Transactional(readOnly = true)
  public PhotoDetailResponse getPhotoById(Long id) {
    Photo photo =
        photoRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    return mapPhotoDetailResponse(photo);
  }

  @Transactional
  public PhotoDetailResponse uploadPhoto(MultipartFile file) {
    ImageProcessingResult imageProcessingResult = imageProcessingService.processImage(file);
    Photo newPhoto =
        Photo.builder()
            .category(null)
            .title(file.getOriginalFilename())
            .filenameThumb(imageProcessingResult.filenameThumb())
            .filenameMedium(imageProcessingResult.filenameMedium())
            .filenameFull(imageProcessingResult.filenameFull())
            .filenameOriginal(file.getOriginalFilename())
            .sortOrder(0)
            .isFeatured(false)
            .description(null)
            .exifData(imageProcessingResult.exifData())
            .width(imageProcessingResult.width())
            .height(imageProcessingResult.height())
            .build();
    return mapPhotoDetailResponse(photoRepository.save(newPhoto));
  }

  @Transactional
  public void deletePhoto(Long id) {
    Photo photo =
        photoRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    try {
      for (String filePath :
          List.of(photo.getFilenameFull(), photo.getFilenameMedium(), photo.getFilenameThumb())) {
        Files.deleteIfExists(Paths.get(storagePath, filePath));
      }
      photoRepository.deleteById(id);
    } catch (Exception e) {
      String msg = "Unable to delete photo with id: " + photo.getId() + " from disk";
      log.error(msg, e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, msg);
    }
  }

  @Transactional
  public PhotoDetailResponse updatePhoto(Long photoId, PhotoUpdateRequest photoUpdateRequest) {
    Photo existingPhoto =
        photoRepository
            .findById(photoId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    Category existingPhotoCategory = null;
    if (photoUpdateRequest.categoryId() != null) {
      existingPhotoCategory =
          categoryRepository
              .findById(photoUpdateRequest.categoryId())
              .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
    existingPhoto.setTitle(photoUpdateRequest.title());
    existingPhoto.setDescription(photoUpdateRequest.description());
    existingPhoto.setSortOrder(photoUpdateRequest.sortOrder());
    existingPhoto.setIsFeatured(photoUpdateRequest.featured());
    existingPhoto.setCategory(existingPhotoCategory);
    return mapPhotoDetailResponse(photoRepository.save(existingPhoto));
  }

  @Transactional
  public void updatePhotoOrder(List<Long> photoIds) {
    for (int i = 0; i < photoIds.size(); i++) {
      photoRepository.updateSortOrderById(photoIds.get(i), i);
    }
  }

  private PhotoDetailResponse mapPhotoDetailResponse(Photo photo) {
    return new PhotoDetailResponse(
        photo.getId(),
        photo.getTitle(),
        THUMB_URL_PREFIX + photo.getFilenameThumb(),
        photo.getPlaceholderBase64(),
        photo.getWidth(),
        photo.getHeight(),
        photo.getIsFeatured(),
        mapCategory(photo),
        photo.getDescription(),
        MEDIUM_URL_PREFIX + photo.getFilenameMedium(),
        FULL_URL_PREFIX + photo.getFilenameFull(),
        photo.getExifData());
  }

  private CategoryResponse mapCategory(Photo photo) {
    if (photo.getCategory() == null) {
      return null;
    }
    Category category = photo.getCategory();
    return new CategoryResponse(
        category.getId(), category.getName(), category.getSlug(), category.getDescription());
  }
}
