package dev.yukmekim.spatialdatastreamingpoc.domain;

import dev.yukmekim.spatialdatastreamingpoc.domain.common.BaseTime;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "T_SCAN_FILE_INFO")
public class ScanFileInfo extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "version_code", nullable = false, unique = true)
    private String versionCode;

    @Column(name = "base_dir_path", nullable = false)
    private String baseDirPath;

    @Column(name = "date")
    private LocalDate date;

    @OneToMany(mappedBy = "scanFileInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScanAreaDataInfo> areaDataInfos = new ArrayList<>();

    @Builder
    public ScanFileInfo(String versionCode, String baseDirPath, LocalDate date) {
        this.versionCode = versionCode;
        this.baseDirPath = baseDirPath;
        this.date = date;
    }
}
