package io.itookthese.api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.itookthese.api.config.GlobalExceptionHandler;
import io.itookthese.api.dto.CategoryResponse;
import io.itookthese.api.dto.PhotoDetailResponse;
import io.itookthese.api.dto.PhotoSummaryResponse;
import io.itookthese.api.service.PhotoService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class PhotoControllerTest {

  @Mock private PhotoService photoService;
  @InjectMocks private PhotoController photoController;
  private MockMvc mockMvc;

  private final CategoryResponse category =
      new CategoryResponse(1L, "Landscape", "landscape", "Landscape photos");

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(photoController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  void getAllPhotos_returnsList() throws Exception {
    PhotoSummaryResponse photo =
        new PhotoSummaryResponse(1L, "Sunset", "/thumb.jpg", null, 800, 600, true, category);
    when(photoService.getAllPhotos(null, null)).thenReturn(List.of(photo));

    mockMvc
        .perform(get("/api/v1/photos"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].title").value("Sunset"));
  }

  @Test
  void getAllPhotos_withCategoryFilter_returnsList() throws Exception {
    when(photoService.getAllPhotos(1L, null)).thenReturn(List.of());

    mockMvc
        .perform(get("/api/v1/photos").param("categoryId", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

  @Test
  void getAllPhotos_withFeaturedFilter_returnsList() throws Exception {
    PhotoSummaryResponse photo =
        new PhotoSummaryResponse(1L, "Featured", "/thumb.jpg", null, 800, 600, true, category);
    when(photoService.getAllPhotos(null, true)).thenReturn(List.of(photo));

    mockMvc
        .perform(get("/api/v1/photos").param("isFeatured", "true"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].isFeatured").value(true));
  }

  @Test
  void getPhotoById_returnsPhoto() throws Exception {
    PhotoDetailResponse detail =
        new PhotoDetailResponse(
            1L, "Sunset", "/thumb.jpg", null, 800, 600, true, category,
            "A beautiful sunset", "/medium.jpg", "/full.jpg", "{\"camera\":\"iPhone 15\"}");
    when(photoService.getPhotoById(1L)).thenReturn(detail);

    mockMvc
        .perform(get("/api/v1/photos/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.description").value("A beautiful sunset"))
        .andExpect(jsonPath("$.fullUrl").value("/full.jpg"));
  }

  @Test
  void getPhotoById_notFound_returns404() throws Exception {
    when(photoService.getPhotoById(999L))
        .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Photo not found"));

    mockMvc.perform(get("/api/v1/photos/999")).andExpect(status().isNotFound());
  }
}
