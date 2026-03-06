package io.itookthese.api.repository;

import io.itookthese.api.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PhotoRepository
    extends JpaRepository<Photo, Long>, JpaSpecificationExecutor<Photo> {

  boolean existsByCategoryId(Long categoryId);

  @Modifying
  @Query("UPDATE Photo p SET p.sortOrder = :sortOrder WHERE p.id = :id")
  void updateSortOrderById(@Param("id") Long id, @Param("sortOrder") Integer sortOrder);
}
