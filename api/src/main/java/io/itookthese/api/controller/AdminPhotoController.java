package io.itookthese.api.controller;

import io.itookthese.api.dto.PhotoDetailResponse;
import io.itookthese.api.dto.PhotoUpdateRequest;
import io.itookthese.api.service.PhotoService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/admin/photos")
@RequiredArgsConstructor
public class AdminPhotoController {

  private final PhotoService photoService;

  @PostMapping
  public ResponseEntity<PhotoDetailResponse> uploadPhoto(@RequestParam("file") MultipartFile file) {
    PhotoDetailResponse photoDetailReponse = photoService.uploadPhoto(file);
    return ResponseEntity.status(HttpStatus.CREATED).body(photoDetailReponse);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePhoto(@PathVariable Long id) {
    photoService.deletePhoto(id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}")
  public ResponseEntity<PhotoDetailResponse> updatePhoto(
      @PathVariable Long id, @RequestBody PhotoUpdateRequest photoUpdateRequest) {
    PhotoDetailResponse photoDetailResponse = photoService.updatePhoto(id, photoUpdateRequest);
    return ResponseEntity.status(HttpStatus.OK).body(photoDetailResponse);
  }

  @PutMapping("/reorder")
  public ResponseEntity<Void> reorderPhotos(@RequestBody List<Long> photoIds) {
    photoService.updatePhotoOrder(photoIds);
    return ResponseEntity.noContent().build();
  }
}
