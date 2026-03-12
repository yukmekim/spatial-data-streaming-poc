# Spatial Data Streaming POC

ROS(Robot Operating System)로부터 수신한 과실 3D 스캔 데이터를 공간 분할(Spatial Partitioning)하여 저장하고, Unity에 스트리밍 방식으로 제공하는 서버의 개념 검증(POC) 프로젝트입니다.

---

## 개요

ROS가 스캔한 과실(사과)의 3D 좌표 및 숙도 정보를 서버에서 수신하면, 전체 스캔 구역을 **40x40 격자(Grid)**로 분할하여 구역별 JSON 파일로 저장합니다. Unity는 전체 데이터를 한 번에 로드하는 대신, 필요한 구역의 파일만 선택적으로 내려받아 렌더링함으로써 메모리 및 성능 부담을 낮춥니다.

---

## 아키텍처

```
[ROS]
  │  POST /api/v1/scan/{versionCode}
  │  Body: List<AppleItemDto> (최대 20만 건 검증 완료)
  ▼
[ScanQueueService]  ← 단일 워커 스레드로 순차 처리 (Race Condition 방지)
  │
  ▼
[ScanService]
  ├── ScanFileInfo 조회/생성 (버전 메타)
  ├── 40x40 격자 분할 (ScanPartitionService)
  ├── 구역별 grade 집계 → DB Upsert (ScanAreaDataInfo)
  └── 구역별 JSON 파일 저장/Append (ScanFileWriteService)

[Unity]
  │  GET /api/v1/unity/scan/{versionCode}/areas      → 구역 메타 목록 조회
  │  GET /api/v1/unity/scan/{versionCode}/data/{fileName} → 구역 파일 다운로드
  ▼
[ScanQueryService]
```

---

## 공간 분할 설계

| 항목 | 값 |
|------|----|
| 전체 구역 | X: -5.0 ~ 5.0 / Z: -5.0 ~ 5.0 |
| 격자 크기 | 0.25 x 0.25 |
| 격자 수 | 40 x 40 = 1,600개 |
| 파일명 형식 | `area_{startX}_{endX}_{startZ}_{endZ}.json` |

---

## 파일 저장 구조

```
/tmp/farm/uploads/
  └── {versionCode}/
      ├── area_-5.0_-4.75_-5.0_-4.75.json
      ├── area_-5.0_-4.75_-4.75_-4.5.json
      └── ...
```

---

## 데이터베이스 구조

### T_SCAN_FILE_INFO
스캔 버전(회차) 메타 정보를 관리합니다.

| 컬럼 | 설명 |
|------|------|
| id | PK |
| version_code | 버전 식별자 (Unique) |
| base_dir_path | 파일 저장 디렉토리 경로 |
| date | 스캔 기준일 |

### T_SCAN_AREA_DATA_INFO
구역별 숙도 통계 및 파일 위치를 관리합니다.

| 컬럼 | 설명 |
|------|------|
| id | PK |
| scan_file_info_id | FK (버전) |
| start_x / end_x / start_z / end_z | 구역 경계 좌표 |
| cnt_grade_0 ~ cnt_grade_3 | 숙도별 과실 수 (분할 수신 시 누적 합산) |
| file_name | 구역 JSON 파일명 |

---

## 기술 스택

- Java 21 / Spring Boot 3.5.0
- Spring Data JPA / H2 (local), Hibernate Batch Insert
- SpringDoc OpenAPI (Swagger UI: `/swagger-ui.html`)
- Lombok
