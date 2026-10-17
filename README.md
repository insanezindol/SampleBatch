# SampleBatch

## 개요

SampleBatch는 별도의 컨테이너를 사용하지 않는 독립형(stand alone) 배치 프로그램입니다. Spring Framework와 MyBatis를 기반으로 구현된 Java 배치 애플리케이션으로, 다양한 서버 환경에서 실행할 수 있도록 설계되었습니다.

> Spring Boot가 아닌 **Spring Framework (XML 설정 기반)** 애플리케이션입니다. `ClassPathXmlApplicationContext`로 컨텍스트를 기동합니다.

## 주요 특징

-   **독립형 배치 프로그램**: 별도의 애플리케이션 서버나 컨테이너 없이 실행 가능
-   **환경별 설정 분리**: 로컬(local), 테스트(tb), 운영(real) 환경별로 구분된 설정 파일
-   **보안 강화**: 데이터베이스 접속 URL/계정/비밀번호 암호화 지원 (AES/CTR)
-   **다중 데이터소스**: Oracle / MS SQL Server 데이터소스별 트랜잭션 매니저 및 MyBatis 매퍼 분리
-   **Maven Wrapper 기반 빌드**: Maven 별도 설치 없이 `mvnw`로 빌드
-   **SQL 로그**: datasource-proxy 를 이용한 SQL 및 바인딩 파라미터 로그

## 기술 스택

| 구분 | 라이브러리 | 버전 |
| --- | --- | --- |
| Language | Java (JDK) | **25** |
| Build | Apache Maven (Maven Wrapper) | 3.9.16 |
| Framework | Spring Framework (context, jdbc, tx) | 7.0.9 |
| Persistence | MyBatis | 3.5.19 |
| Persistence | MyBatis-Spring | 4.1.0 |
| Connection Pool | Apache Commons DBCP2 | 2.14.0 |
| JDBC Driver | Oracle JDBC (`ojdbc17`) | 23.26.3.0.0 |
| JDBC Driver | Microsoft SQL Server JDBC (`mssql-jdbc`) | 13.6.1.jre11 |
| SQL Log | datasource-proxy | 1.11.1 |
| Logging | SLF4J | 2.0.20 |
| Logging | Log4j2 (`log4j-core`, `log4j-slf4j2-impl`) | 2.26.1 |
| Util | Lombok | 1.18.48 |

## 프로젝트 구조

```
.
├── mvnw, mvnw.cmd, .mvn/        # Maven Wrapper
├── pom.xml
└── src/
    └── main/
        ├── java/
        │   └── com/wiz/
        │       ├── common/
        │       │   ├── constants/   # 상수 정의 (Constants.configProp)
        │       │   ├── models/      # 기본 모델 클래스
        │       │   └── util/        # 암호화 DataSource (EncriptBasicDataSource)
        │       └── samplebatch/
        │           ├── main/        # 메인 클래스 (Main)
        │           └── test/        # 테스트 서비스 및 매퍼 (Mapper XML 포함)
        └── resources/
            ├── context/             # Spring XML 설정 파일
            ├── log4j/               # 환경별 Log4j2 설정 파일
            └── properties/          # 환경별 속성 파일
```

> MyBatis Mapper XML(`*.xml`)은 `src/main/java` 의 Mapper 인터페이스와 같은 위치에 있으며, `pom.xml` 의 `<resources>` 설정으로 빌드 결과물에 포함됩니다.

## 환경 설정

### 지원 환경

-   `local`: 로컬 개발 환경
-   `tb`: 테스트 환경
-   `real`: 운영 환경

### 설정 파일 구조

각 환경별로 다음 설정 파일들이 제공됩니다:

-   `context/{환경}_BaseContext.xml`: Spring 기본 컨텍스트 설정 (속성 파일 로드 후 `ApplicationContext.xml` import)
-   `properties/{환경}_properties.xml`: 환경별 속성 (DB 접속 정보는 암호화된 값)
-   `log4j/{환경}_log4j2.xml`: 환경별 Log4j2 설정 (로그 레벨, 로그 파일 경로)

### 설정 로드 순서

1. `Main.java` (메인 클래스, 실행 인자로 환경 결정)
2. `{환경}_log4j2.xml` (Main 에서 Log4j2 초기화)
3. `{환경}_BaseContext.xml` (환경별 기본 컨텍스트)
4. `{환경}_properties.xml` (환경별 속성)
5. `ApplicationContext.xml` (공통 애플리케이션 컨텍스트 - DataSource, 트랜잭션, MyBatis)

## 설치 및 실행

### 필요 조건

-   **JDK 25** 이상
-   Maven 은 별도 설치 불필요 (Maven Wrapper 사용, 최초 실행 시 Maven 3.9.16 자동 다운로드)

`JAVA_HOME` 이 JDK 25 를 가리키도록 설정합니다.

```powershell
# Windows (PowerShell)
$env:JAVA_HOME = "C:\java\jdk-25.0.4.1"
```

```bash
# Linux / Git Bash
export JAVA_HOME=/path/to/jdk-25
```

### 빌드

```bash
# Windows
mvnw.cmd clean package

# Linux / macOS / Git Bash
./mvnw clean package
```

빌드 결과물:

```
target/
├── SampleBatch.jar   # 실행 jar (MANIFEST 에 Main-Class, Class-Path: lib/*.jar 포함)
└── lib/              # 의존 라이브러리
```

### 실행

```bash
java -jar target/SampleBatch.jar [환경]
```

**환경 매개변수:**

-   `local`: 로컬 환경에서 실행
-   `tb`: 테스트 환경에서 실행
-   `real`: 운영 환경에서 실행

**실행 예시:**

```bash
# 로컬 환경에서 실행
java -jar target/SampleBatch.jar local

# 테스트 환경에서 실행
java -jar target/SampleBatch.jar tb

# 운영 환경에서 실행
java -jar target/SampleBatch.jar real
```

### 배포

`SampleBatch.jar` 와 `lib/` 폴더를 같은 위치에 복사한 후 실행합니다. 셸 스크립트로 실행할 경우 jar 경로는 절대 경로로 지정합니다.

```
{배포 루트}/
├── SampleBatch.jar
├── run.sh
└── lib/
    └── *.jar
```

```bash
#!/bin/sh
java -jar /batch/SampleBatch/SampleBatch.jar real
```

## 데이터베이스 지원

JDBC 드라이버는 Maven Central 의 공식 아티팩트를 사용합니다. (기존 `src/main/resources/lib` 의 로컬 jar 는 제거)

-   **Oracle Database**: `com.oracle.database.jdbc:ojdbc17` (Driver: `oracle.jdbc.OracleDriver`)
    -   Oracle 23ai JDBC 드라이버는 Oracle DB **19c 이상**을 지원합니다. 그 이전 버전(10g/11g/12c 등)의 DB 에 접속해야 한다면 해당 DB 버전을 지원하는 드라이버로 교체가 필요합니다.
-   **Microsoft SQL Server**: `com.microsoft.sqlserver:mssql-jdbc` (Driver: `com.microsoft.sqlserver.jdbc.SQLServerDriver`)
    -   mssql-jdbc 10.x 부터 기본값이 `encrypt=true` 입니다. 신뢰할 수 있는 인증서가 없는 서버라면 접속 URL 에 `encrypt=false` 또는 `trustServerCertificate=true` 설정이 필요할 수 있습니다.

## 보안

-   데이터베이스 접속 URL / 계정 / 비밀번호는 암호화된 값으로 `{환경}_properties.xml` 에 저장합니다.
-   `EncriptBasicDataSource`(DBCP2 `BasicDataSource` 확장)가 `setUrl`, `setUsername`, `setPassword` 시점에 자동으로 복호화합니다.
-   암호화 값은 `EncriptBasicDataSource.encriptString(평문)` 으로 생성합니다.

## 로깅

-   SLF4J + Log4j2 를 사용합니다. 환경별 설정은 `log4j/{환경}_log4j2.xml` 입니다.
-   콘솔과 파일(`service.out`, 일 단위 롤링 `service.out.yyyy-MM-dd.log`)로 출력합니다.

| 환경 | Root 레벨 | 로그 경로 |
| --- | --- | --- |
| local | DEBUG | `C:/_Log/BrmCmStatBatch` |
| tb | INFO | `/home/condev/log/BrmCmStatBatch` |
| real | INFO | `/home/xring/log/BrmCmStatBatch` |

-   **SQL 로그**: datasource-proxy 가 실행된 SQL 과 바인딩 파라미터를 `jdbc.query` 로거(INFO)로 출력합니다. SQL 로그를 끄려면 Log4j2 설정에서 `jdbc.query` 로거 레벨을 `WARN` 이상으로 변경합니다.

## 트랜잭션

-   명시적 트랜잭션 방식으로, 트랜잭션이 필요한 메소드에 트랜잭션 매니저를 지정하여 `@Transactional` 을 선언합니다.

```java
@Transactional(value = "ktmusic_transactionManager", rollbackFor = Exception.class)
public void runTest2() throws Exception { ... }
```

## 스케줄링

이 배치 프로그램은 다음과 같은 스케줄링 도구와 함께 사용할 수 있습니다:

-   **Jenkins**: CI/CD 파이프라인과 연동 (`mvnw clean package` 후 `SampleBatch.jar` 와 `lib/` 배포)
-   **Cron**: Linux/Unix 시스템의 스케줄러
-   **Windows Task Scheduler**: Windows 환경의 작업 스케줄러
