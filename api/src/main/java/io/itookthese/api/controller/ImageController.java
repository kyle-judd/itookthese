package io.itookthese.api.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/images")
public class ImageController {

  @Value("${storage.path}")
  private String storagePath;

  @GetMapping("/{size}/{filename}")
  public ResponseEntity<Resource> serveImage(
      @PathVariable String size, @PathVariable String filename) {
    if (!size.matches("thumb|medium|full")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid size");
    }

    // Prevent path traversal
    if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    try {
      Path filePath = Paths.get(storagePath).resolve(filename).normalize();
      if (!filePath.startsWith(Paths.get(storagePath).normalize())) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
      }

      Resource resource = new UrlResource(filePath.toUri());
      if (!resource.exists() || !resource.isReadable()) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
      }

      String contentType = Files.probeContentType(filePath);
      if (contentType == null) {
        contentType = "application/octet-stream";
      }

      return ResponseEntity.ok()
          .contentType(MediaType.parseMediaType(contentType))
          .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000, immutable")
          .body(resource);
    } catch (ResponseStatusException e) {
      throw e;
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
