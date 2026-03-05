package io.itookthese.api.specification;

import io.itookthese.api.entity.Photo;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class PhotoSpecification {
  public static Specification<Photo> withFilters(Long categoryId, Boolean isFeatured) {
    return (root, query, cb) -> {
        List<Predicate> predicates = new ArrayList<>();

        if (categoryId != null) {
            predicates.add(cb.equal(root.get("category").get("id"), categoryId));
        }

        if (isFeatured != null) {
            predicates.add(cb.equal(root.get("isFeatured"), isFeatured));
        }

        return cb.and(predicates.toArray(new Predicate[0]));

    };
  }
}
