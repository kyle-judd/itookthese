package io.itookthese.api.repository;

import io.itookthese.api.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PhotoRepository
    extends JpaRepository<Photo, Long>, JpaSpecificationExecutor<Photo> {}
