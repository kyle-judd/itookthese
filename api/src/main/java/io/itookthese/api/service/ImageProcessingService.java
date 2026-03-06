package io.itookthese.api.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.itookthese.api.dto.ImageProcessingResult;
import io.itookthese.api.enums.ImageType;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageProcessingService {

  private final ObjectMapper objectMapper;

  @Value("${storage.path}")
  private String storagePath;

  public ImageProcessingResult processImage(MultipartFile file) {
    String contentType = file.getContentType();
    if (contentType == null
        || !List.of(
                ImageType.JPG.getMimeType(),
                ImageType.JPEG.getMimeType(),
                ImageType.PNG.getMimeType(),
                ImageType.HEIC.getMimeType(),
                ImageType.HEIF.getMimeType())
            .contains(contentType)) {
      throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }
    if (file.getSize() > 30 * 1024 * 1024) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File too large");
    }

    List<Path> createdFiles = new ArrayList<>();
    try {
      String exifData = extractExifData(file);
      String originalFilename = file.getOriginalFilename();
      if (originalFilename == null || originalFilename.isBlank()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid filename");
      }
      String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
      String uuid = UUID.randomUUID().toString();
      String thumbFileName = uuid + "_thumb." + extension;
      String mediumFileName = uuid + "_medium." + extension;
      String fullFileName = uuid + "_full." + extension;
      byte[] bytes = file.getBytes();
      BufferedImage original = ImageIO.read(new ByteArrayInputStream(bytes));
      if (original == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not read image");
      }
      int width = original.getWidth();
      int height = original.getHeight();
      Path thumbPath = Paths.get(storagePath, thumbFileName);
      Thumbnails.of(new ByteArrayInputStream(bytes))
          .size(400, Integer.MAX_VALUE)
          .keepAspectRatio(true)
          .toFile(thumbPath.toFile());
      createdFiles.add(thumbPath);
      Path mediumPath = Paths.get(storagePath, mediumFileName);
      Thumbnails.of(new ByteArrayInputStream(bytes))
          .size(1200, Integer.MAX_VALUE)
          .keepAspectRatio(true)
          .toFile(mediumPath.toFile());
      createdFiles.add(mediumPath);
      Path fullPath = Paths.get(storagePath, fullFileName);
      Thumbnails.of(new ByteArrayInputStream(bytes))
          .size(2400, Integer.MAX_VALUE)
          .keepAspectRatio(true)
          .toFile(fullPath.toFile());
      createdFiles.add(fullPath);
      return new ImageProcessingResult(
          thumbFileName, mediumFileName, fullFileName, "", exifData, width, height);
    } catch (IOException | ImageProcessingException e) {
      for (Path path : createdFiles) {
        try {
          Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
      }
      log.error("While processing image", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private String extractExifData(MultipartFile file) throws IOException, ImageProcessingException {
    Metadata metadata = ImageMetadataReader.readMetadata(file.getInputStream());
    Map<String, String> exifMap = new HashMap<>();

    ExifIFD0Directory ifd0 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
    if (ifd0 != null) {
      exifMap.put("make", ifd0.getString(ExifIFD0Directory.TAG_MAKE));
      exifMap.put("model", ifd0.getString(ExifIFD0Directory.TAG_MODEL));
    }

    ExifSubIFDDirectory subIFD = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
    if (subIFD != null) {
      exifMap.put("iso", subIFD.getDescription(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT));
      exifMap.put("aperture", subIFD.getDescription(ExifSubIFDDirectory.TAG_APERTURE));
      exifMap.put("shutterSpeed", subIFD.getDescription(ExifSubIFDDirectory.TAG_SHUTTER_SPEED));
      exifMap.put("focalLength", subIFD.getDescription(ExifSubIFDDirectory.TAG_FOCAL_LENGTH));
    }

    return objectMapper.writeValueAsString(exifMap);
  }
}
