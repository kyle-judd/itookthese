package io.itookthese.api.repository;

import io.itookthese.api.entity.Photo;
import org.springframework.data.repository.Repository;

interface PhotoRepository extends Repository<Photo, Long> {
}
