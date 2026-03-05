package io.itookthese.api.repository;

import io.itookthese.api.entity.ContactSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactSubmissionRepository extends JpaRepository<ContactSubmission, Long> {}
