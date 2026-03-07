package io.itookthese.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.itookthese.api.config.GlobalExceptionHandler;
import io.itookthese.api.dto.PhotoDetailResponse;
import io.itookthese.api.dto.PhotoUpdateRequest;
import io.itookthese.api.service.PhotoService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AdminPhotoControllerTest {

  @Mock private PhotoService photoService;
  @InjectMocks private AdminPhotoController adminPhotoController;
  private MockMvc mockMvc;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(adminPhotoController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  private PhotoDetailResponse samplePhoto() {
    return new PhotoDetailResponse(
        1L, "Test Photo", "Description", "/thumb.jpg", "/medium.jpg", "/full.jpg",
        null, 800, 600, false, "Landscape", 1L, 0,
        null, null, null, null, null, null, null);
  }

  @Test
  void uploadPhoto_returns201() throws Exception {
    MockMultipartFile file =
        new MockMultipartFile("file", "photo.jpg", "image/jpeg", "fake-image".getBytes());
    when(photoService.uploadPhoto(any())).thenReturn(samplePhoto());

    mockMvc
        .perform(multipart("/api/v1/admin/photos").file(file))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.title").value("Test Photo"));
  }

  @Test
  void deletePhoto_returns204() throws Exception {
    doNothing().when(photoService).deletePhoto(1L);

    mockMvc
        .perform(delete("/api/v1/admin/photos/1"))
        .andExpect(status().isNoContent());
  }

  @Test
  void updatePhoto_returns200() throws Exception {
    PhotoUpdateRequest updateRequest = new PhotoUpdateRequest(1L, "Updated Title", "Updated desc", 1, true);
    PhotoDetailResponse updated =
        new PhotoDetailResponse(
            1L, "Updated Title", "Updated desc", "/thumb.jpg", "/medium.jpg", "/full.jpg",
            null, 800, 600, true, "Landscape", 1L, 1,
            null, null, null, null, null, null, null);
    when(photoService.updatePhoto(eq(1L), any())).thenReturn(updated);

    mockMvc
        .perform(
            put("/api/v1/admin/photos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Updated Title"))
        .andExpect(jsonPath("$.isFeatured").value(true));
  }

  @Test
  void reorderPhotos_returns204() throws Exception {
    doNothing().when(photoService).updatePhotoOrder(any());

    mockMvc
        .perform(
            put("/api/v1/admin/photos/reorder")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(3L, 1L, 2L))))
        .andExpect(status().isNoContent());
  }
}
