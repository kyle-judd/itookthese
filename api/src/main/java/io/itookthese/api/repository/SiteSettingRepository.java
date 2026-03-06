package io.itookthese.api.repository;

import io.itookthese.api.entity.SiteSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteSettingRepository extends JpaRepository<SiteSetting, String> {}
