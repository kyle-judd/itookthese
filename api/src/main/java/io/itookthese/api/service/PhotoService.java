package io.itookthese.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.itookthese.api.dto.*;
import io.itookthese.api.entity.Category;
import io.itookthese.api.entity.Photo;
import io.itookthese.api.repository.CategoryRepository;
import io.itookthese.api.repository.PhotoRepository;
import io.itookthese.api.specification.PhotoSpecification;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
  private final ObjectMapper objectMapper;

  @Value("${storage.path}")
  private String storagePath;

  @Transactional(readOnly = true)
  public List<PhotoSummaryResponse> getAllPhotos(Long categoryId, Boolean isFeatured) {
    return photoRepository
        .findAll(
            PhotoSpecification.withFilters(categoryId, isFeatured),
            Sort.by(Sort.Direction.ASC, "sortOrder"))
        .stream()
        .map(this::mapPhotoSummaryResponse)
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
            .placeholderBase64(imageProcessingResult.placeholderBase64())
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
    if (photoUpdateRequest.title() != null) {
      existingPhoto.setTitle(photoUpdateRequest.title());
    }
    if (photoUpdateRequest.description() != null) {
      existingPhoto.setDescription(photoUpdateRequest.description());
    }
    if (photoUpdateRequest.sortOrder() != null) {
      existingPhoto.setSortOrder(photoUpdateRequest.sortOrder());
    }
    if (photoUpdateRequest.featured() != null) {
      existingPhoto.setIsFeatured(photoUpdateRequest.featured());
    }
    if (photoUpdateRequest.categoryId() != null) {
      Category category =
          categoryRepository
              .findById(photoUpdateRequest.categoryId())
              .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
      existingPhoto.setCategory(category);
    }
    return mapPhotoDetailResponse(photoRepository.save(existingPhoto));
  }

  @Transactional
  public void updatePhotoOrder(List<Long> photoIds) {
    for (int i = 0; i < photoIds.size(); i++) {
      photoRepository.updateSortOrderById(photoIds.get(i), i);
    }
  }

  private PhotoSummaryResponse mapPhotoSummaryResponse(Photo photo) {
    Category category = photo.getCategory();
    return new PhotoSummaryResponse(
        photo.getId(),
        photo.getTitle(),
        photo.getDescription(),
        THUMB_URL_PREFIX + photo.getFilenameThumb(),
        MEDIUM_URL_PREFIX + photo.getFilenameMedium(),
        FULL_URL_PREFIX + photo.getFilenameFull(),
        photo.getPlaceholderBase64(),
        photo.getWidth(),
        photo.getHeight(),
        photo.getIsFeatured(),
        category != null ? category.getName() : null,
        category != null ? category.getId() : null,
        photo.getSortOrder());
  }

  private PhotoDetailResponse mapPhotoDetailResponse(Photo photo) {
    Category category = photo.getCategory();
    Map<String, String> exif = parseExif(photo.getExifData());
    String cameraModel = exif.get("model");
    if (cameraModel != null && exif.get("make") != null) {
      String make = exif.get("make");
      if (!cameraModel.startsWith(make)) {
        cameraModel = make + " " + cameraModel;
      }
    }
    return new PhotoDetailResponse(
        photo.getId(),
        photo.getTitle(),
        photo.getDescription(),
        THUMB_URL_PREFIX + photo.getFilenameThumb(),
        MEDIUM_URL_PREFIX + photo.getFilenameMedium(),
        FULL_URL_PREFIX + photo.getFilenameFull(),
        photo.getPlaceholderBase64(),
        photo.getWidth(),
        photo.getHeight(),
        photo.getIsFeatured(),
        category != null ? category.getName() : null,
        category != null ? category.getId() : null,
        photo.getSortOrder(),
        cameraModel,
        null,
        exif.get("focalLength"),
        exif.get("aperture"),
        exif.get("shutterSpeed"),
        exif.get("iso"),
        photo.getCreatedAt() != null ? photo.getCreatedAt().toString() : null);
  }

  private Map<String, String> parseExif(String exifData) {
    if (exifData == null || exifData.isBlank()) {
      return Collections.emptyMap();
    }
    try {
      return objectMapper.readValue(exifData, new TypeReference<>() {});
    } catch (Exception e) {
      log.warn("Failed to parse EXIF data", e);
      return Collections.emptyMap();
    }
  }
}
