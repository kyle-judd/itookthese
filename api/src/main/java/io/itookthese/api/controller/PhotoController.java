package io.itookthese.api.controller;

import io.itookthese.api.dto.PhotoDetailResponse;
import io.itookthese.api.dto.PhotoSummaryResponse;
import io.itookthese.api.service.PhotoService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/photos")
@RequiredArgsConstructor
public class PhotoController {
  private final PhotoService photoService;

  @GetMapping
  public List<PhotoSummaryResponse> getAllPhotos(
      @RequestParam(required = false) Long categoryId,
      @RequestParam(required = false) Boolean isFeatured) {
    return photoService.getAllPhotos(categoryId, isFeatured);
  }

  @GetMapping("/{id}")
  public PhotoDetailResponse getPhotoById(@PathVariable Long id) {
    return photoService.getPhotoById(id);
  }
}
