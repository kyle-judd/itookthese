package io.itookthese.api.service;

import io.itookthese.api.dto.CategoryResponse;
import io.itookthese.api.dto.PhotoDetailResponse;
import io.itookthese.api.dto.PhotoSummaryResponse;
import io.itookthese.api.entity.Category;
import io.itookthese.api.entity.Photo;
import io.itookthese.api.repository.PhotoRepository;
import io.itookthese.api.specification.PhotoSpecification;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PhotoService {
  private final PhotoRepository photoRepository;

  @Transactional(readOnly = true)
  public List<PhotoSummaryResponse> getAllPhotos(Long categoryId, Boolean isFeatured) {
    return photoRepository.findAll(PhotoSpecification.withFilters(categoryId, isFeatured)).stream()
        .map(
            photo ->
                new PhotoSummaryResponse(
                    photo.getId(),
                    photo.getTitle(),
                    "/api/v1/images/thumb/" + photo.getFilenameThumb(),
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
    return new PhotoDetailResponse(
        photo.getId(),
        photo.getTitle(),
        "/api/v1/images/thumb/" + photo.getFilenameThumb(),
        photo.getPlaceholderBase64(),
        photo.getWidth(),
        photo.getHeight(),
        photo.getIsFeatured(),
        mapCategory(photo),
        photo.getDescription(),
        "/api/v1/images/medium/" + photo.getFilenameMedium(),
        "/api/v1/images/full/" + photo.getFilenameFull(),
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
