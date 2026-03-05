package io.itookthese.api.repository;

import io.itookthese.api.entity.Category;
import org.springframework.data.repository.Repository;

interface CategoryRepository extends Repository<Category, Long> {
}
