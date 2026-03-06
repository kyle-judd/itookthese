package io.itookthese.api.repository;

import io.itookthese.api.entity.SiteSetting;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteSettingRepository extends JpaRepository<SiteSetting, String> {
    Optional<SiteSetting> findByKey(String key);
}
