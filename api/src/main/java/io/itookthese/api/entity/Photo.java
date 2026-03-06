package io.itookthese.api.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "photos")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Photo {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  private Category category;

  private String title;
  private String description;

  @Column(name = "filename_original", nullable = false)
  private String filenameOriginal;

  @Column(name = "filename_thumb", nullable = false)
  private String filenameThumb;

  @Column(name = "filename_medium", nullable = false)
  private String filenameMedium;

  @Column(name = "filename_full", nullable = false)
  private String filenameFull;

  @Column(name = "placeholder_base64")
  private String placeholderBase64;

  @Column(nullable = false)
  private Integer width;

  @Column(nullable = false)
  private Integer height;

  @Column(name = "exif_data", columnDefinition = "jsonb")
  private String exifData;

  @Column(name = "sort_order", nullable = false)
  private Integer sortOrder;

  @Column(name = "is_featured", nullable = false)
  private Boolean isFeatured;

  @Column(name = "created_at", nullable = false)
  @CreationTimestamp
  private OffsetDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  @UpdateTimestamp
  private OffsetDateTime updatedAt;
}
