package dev.yukmekim.spatialdatastreamingpoc.domain;

import dev.yukmekim.spatialdatastreamingpoc.domain.common.BaseTime;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "T_SCAN_AREA_DATA_INFO")
public class ScanAreaDataInfo extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scan_file_info_id", nullable = false)
    private ScanFileInfo scanFileInfo;

    @Column(name = "sort")
    private Integer sort;

    @Column(name = "start_x", nullable = false)
    private Float startX;

    @Column(name = "end_x", nullable = false)
    private Float endX;

    @Column(name = "start_z", nullable = false)
    private Float startZ;

    @Column(name = "end_z", nullable = false)
    private Float endZ;

    @Column(name = "cnt_grade_0", nullable = false)
    private int cntGrade0;

    @Column(name = "cnt_grade_1", nullable = false)
    private int cntGrade1;

    @Column(name = "cnt_grade_2", nullable = false)
    private int cntGrade2;

    @Column(name = "cnt_grade_3", nullable = false)
    private int cntGrade3;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Builder
    public ScanAreaDataInfo(ScanFileInfo scanFileInfo, Integer sort,
                            Float startX, Float endX, Float startZ, Float endZ,
                            int cntGrade0, int cntGrade1, int cntGrade2, int cntGrade3,
                            String fileName) {
        this.scanFileInfo = scanFileInfo;
        this.sort = sort;
        this.startX = startX;
        this.endX = endX;
        this.startZ = startZ;
        this.endZ = endZ;
        this.cntGrade0 = cntGrade0;
        this.cntGrade1 = cntGrade1;
        this.cntGrade2 = cntGrade2;
        this.cntGrade3 = cntGrade3;
        this.fileName = fileName;
    }

    public void accumulateCounts(int grade0, int grade1, int grade2, int grade3) {
        this.cntGrade0 += grade0;
        this.cntGrade1 += grade1;
        this.cntGrade2 += grade2;
        this.cntGrade3 += grade3;
    }
}
