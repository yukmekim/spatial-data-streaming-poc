package dev.yukmekim.spatialdatastreamingpoc.repository;

import dev.yukmekim.spatialdatastreamingpoc.domain.ScanAreaDataInfo;
import dev.yukmekim.spatialdatastreamingpoc.domain.ScanFileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ScanAreaDataInfoRepository extends JpaRepository<ScanAreaDataInfo, Long> {

    // 특정 버전의 전체 구역 목록 조회
    List<ScanAreaDataInfo> findByScanFileInfo(ScanFileInfo scanFileInfo);

    // 특정 버전의 전체 구역을 ID 기반으로 조회 (N+1 방지용 Fetch Join)
    @Query("SELECT a FROM ScanAreaDataInfo a JOIN FETCH a.scanFileInfo WHERE a.scanFileInfo.id = :scanFileInfoId")
    List<ScanAreaDataInfo> findAllByScanFileInfoIdWithFetch(@Param("scanFileInfoId") Long scanFileInfoId);
}
