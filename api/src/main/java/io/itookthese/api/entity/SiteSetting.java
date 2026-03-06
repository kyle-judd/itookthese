package io.itookthese.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "site_settings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteSetting {
  @Id private String key;

  @Column(nullable = false)
  private String value;

  @Column(name = "updated_at", nullable = false)
  @UpdateTimestamp
  private OffsetDateTime updatedAt;
}
