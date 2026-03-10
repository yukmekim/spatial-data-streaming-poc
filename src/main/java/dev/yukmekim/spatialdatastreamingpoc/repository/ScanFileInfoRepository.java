package dev.yukmekim.spatialdatastreamingpoc.repository;

import dev.yukmekim.spatialdatastreamingpoc.domain.ScanFileInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScanFileInfoRepository extends JpaRepository<ScanFileInfo, Long> {

    Optional<ScanFileInfo> findByVersionCode(String versionCode);
}
