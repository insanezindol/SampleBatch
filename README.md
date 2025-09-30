# SampleBatch

## 개요

SampleBatch는 별도의 컨테이너를 사용하지 않는 독립형(stand alone) 배치 프로그램입니다. Spring Framework와 MyBatis를 기반으로 구현된 Java 배치 애플리케이션으로, 다양한 서버 환경에서 실행할 수 있도록 설계되었습니다.

## 주요 특징

-   **독립형 배치 프로그램**: 별도의 애플리케이션 서버나 컨테이너 없이 실행 가능
-   **환경별 설정 분리**: 로컬(local), 테스트(tb), 운영(real) 환경별로 구분된 설정 파일
-   **보안 강화**: 데이터베이스 접속 비밀번호 암호화 지원
-   **Maven 기반 의존성 관리**: 편리한 라이브러리 관리 및 빌드 프로세스
-   **Spring Framework**: IoC 컨테이너 및 트랜잭션 관리
-   **MyBatis**: 객체 관계 매핑 및 SQL 처리

## 기술 스택

-   **Java**: 핵심 프로그래밍 언어
-   **Spring Framework 4.2.1**: IoC 컨테이너, 의존성 주입
-   **MyBatis 3.4.0**: 데이터베이스 매핑 프레임워크
-   **Apache Commons DBCP**: 데이터베이스 연결 풀
-   **Log4j**: 로깅 프레임워크
-   **Maven**: 빌드 및 의존성 관리 도구

## 프로젝트 구조

```
src/
├── main/
│   ├── java/
│   │   └── com/wiz/
│   │       ├── common/          # 공통 유틸리티 및 상수
│   │       │   ├── constants/   # 상수 정의
│   │       │   ├── models/      # 기본 모델 클래스
│   │       │   └── util/        # 암호화 등 유틸리티
│   │       └── samplebatch/     # 메인 배치 로직
│   │           ├── main/        # 메인 클래스
│   │           └── test/        # 테스트 서비스 및 매퍼
│   └── resources/
│       ├── context/             # Spring 설정 파일
│       ├── lib/                 # 외부 라이브러리 (JDBC 드라이버)
│       ├── log4j/              # 로그 설정 파일
│       └── properties/         # 환경별 속성 파일
```

## 환경 설정

### 지원 환경

-   `local`: 로컬 개발 환경
-   `tb`: 테스트 환경
-   `real`: 운영 환경

### 설정 파일 구조

각 환경별로 다음 설정 파일들이 제공됩니다:

-   `{환경}_BaseContext.xml`: Spring 기본 컨텍스트 설정
-   `{환경}_properties.xml`: 환경별 속성 설정
-   `{환경}_log4j.xml`: 로그 설정

### 설정 로드 순서

1. `Main.java` (메인 클래스)
2. `{서버타입}_BaseContext.xml` (환경별 기본 컨텍스트)
3. `{서버타입}_properties.xml` (환경별 속성)
4. `{서버타입}_log4j.xml` (환경별 로그 설정)
5. `ApplicationContext.xml` (공통 애플리케이션 컨텍스트)

## 설치 및 실행

### 필요 조건

-   Java 8 이상
-   Maven 3.0 이상

### 빌드

```bash
mvn clean compile
```

### 실행

```bash
java -cp target/classes com.wiz.samplebatch.main.Main [환경]
```

**환경 매개변수:**

-   `local`: 로컬 환경에서 실행
-   `tb`: 테스트 환경에서 실행
-   `real`: 운영 환경에서 실행

**실행 예시:**

```bash
# 로컬 환경에서 실행
java -cp target/classes com.wiz.samplebatch.main.Main local

# 테스트 환경에서 실행
java -cp target/classes com.wiz.samplebatch.main.Main tb

# 운영 환경에서 실행
java -cp target/classes com.wiz.samplebatch.main.Main real
```

## 데이터베이스 지원

이 프로젝트는 다음 데이터베이스를 지원합니다:

-   **Oracle Database**: OJDBC 드라이버 (10.2.0.4.0, 12.1.0.2.0)
-   **Microsoft SQL Server**: SQL Server JDBC 드라이버 (sqljdbc4)

## 보안

-   데이터베이스 접속 비밀번호는 `EncriptBasicDataSource` 클래스를 통해 암호화되어 저장됩니다
-   연결 시 자동으로 복호화되어 사용됩니다

## 로깅

-   Log4j를 사용한 체계적인 로그 관리
-   환경별로 다른 로그 레벨 및 출력 위치 설정 가능
-   SQL 로그 확장 기능 제공 (log4jdbc-remix)

## 스케줄링

이 배치 프로그램은 다음과 같은 스케줄링 도구와 함께 사용할 수 있습니다:

-   **Jenkins**: CI/CD 파이프라인과 연동
-   **Cron**: Linux/Unix 시스템의 스케줄러
-   **Windows Task Scheduler**: Windows 환경의 작업 스케줄러
