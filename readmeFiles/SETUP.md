# 설치 및 실행 가이드

## 목차

1. [사전 요구사항](#사전-요구사항)
2. [프로젝트 설정](#프로젝트-설정)
3. [데이터베이스 설정](#데이터베이스-설정)
4. [애플리케이션 설정](#애플리케이션-설정)
5. [빌드 및 실행](#빌드-및-실행)
6. [문제 해결](#문제-해결)

---

## 사전 요구사항

### 시스템 요구사항

- **OS**: Windows, macOS, Linux
- **Java**: JDK 17 이상
- **MySQL**: 8.0 이상
- **Gradle**: 9.0 이상

### 필수 소프트웨어 설치

#### 1. Java 17 설치 확인

```bash
java -version
javac -version
```

Java가 설치되지 않았다면 [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) 또는 [OpenJDK](https://adoptium.net/) 설치

#### 2. MySQL 설치

**macOS (Homebrew 사용)**
```bash
brew install mysql
brew services start mysql
```

**Windows**
[MySQL Community Downloads](https://dev.mysql.com/downloads/mysql/) 에서 설치

**Linux (Ubuntu/Debian)**
```bash
sudo apt-get install mysql-server
sudo systemctl start mysql
```

#### 3. Gradle 설정

프로젝트에 포함된 Gradle Wrapper를 사용하므로 별도 설치 불필요

---

## 프로젝트 설정

### 1. 리포지토리 클론

```bash
git clone https://github.com/your-organization/dangdang-crawling.git
cd dangdang-crawling
```

### 2. IDE 열기

**IntelliJ IDEA**
1. File → Open → dangdang-crawling 폴더 선택
2. 프로젝트 로드 및 Gradle 동기화 대기

**VS Code**
1. File → Open Folder → dangdang-crawling 폴더 선택
2. Extension Pack for Java 설치

---

## 데이터베이스 설정

### 1. MySQL 접속

```bash
mysql -u root -p
```

### 2. 데이터베이스 생성

```sql
CREATE DATABASE dangdang_crawling CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE dangdang_crawling;
```

### 3. 테이블 생성

```bash
# DDL 파일 실행 (해당 파일이 있는 경우)
mysql -u root -p dangdang_crawling < src/main/resources/mapper/ddl/schema.sql
```

또는 [DATABASE.md](./DATABASE.md)의 테이블 생성 쿼리를 직접 실행

### 4. 사용자 생성 (선택사항)

```sql
-- 새로운 사용자 생성
CREATE USER 'crawling'@'localhost' IDENTIFIED BY 'password123';

-- 권한 부여
GRANT ALL PRIVILEGES ON dangdang_crawling.* TO 'crawling'@'localhost';
FLUSH PRIVILEGES;
```

---

## 애플리케이션 설정

### 1. application.yaml 설정

`src/main/resources/application.yaml` 파일 생성 또는 수정:

```yaml
spring:
  application:
    name: dangdang-crawling
  
  datasource:
    url: jdbc:mysql://localhost:3306/dangdang_crawling?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=utf8mb4
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: validate
  
  # Servlet 설정
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

# MyBatis 설정
mybatis:
  mapper-locations: classpath:mapper/**/*.xml
  type-aliases-package: com.dangdang.crawling.biz.domain.entity

# Logging 설정
logging:
  level:
    root: INFO
    com.dangdang.crawling: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/application.log
    max-size: 10MB
    max-history: 10

# 서버 포트
server:
  port: 8080
  servlet:
    context-path: /
```

### 2. 환경 변수 설정 (선택사항)

`.env` 파일 생성:

```properties
DB_HOST=localhost
DB_PORT=3306
DB_NAME=dangdang_crawling
DB_USERNAME=root
DB_PASSWORD=your_password
SERVER_PORT=8080
```

`application.yaml`에서 환경 변수 참조:

```yaml
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:dangdang_crawling}
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD}
```

---

## 빌드 및 실행

### 1. 프로젝트 빌드

```bash
# 전체 빌드 (테스트 포함)
./gradlew clean build

# 빌드 (테스트 제외)
./gradlew clean build -x test

# 특정 구성으로 빌드
./gradlew clean build -Dspring.profiles.active=prod
```

### 2. 애플리케이션 실행

**방법 1: Gradle 사용**
```bash
./gradlew bootRun
```

**방법 2: JAR 파일 실행**
```bash
# 빌드 후 JAR 파일 생성
./gradlew bootJar

# JAR 파일 실행
java -jar build/libs/dangdang-crawling-0.0.1-SNAPSHOT.jar
```

**방법 3: IDE 사용**
- IntelliJ IDEA: Run → Run 'DangdangCrawlingApplication'
- VS Code: F5 또는 Run → Start Debugging

### 3. 애플리케이션 접속

```
http://localhost:8080/api/crawling/jobs
```

정상 작동 시 크롤링 작업 목록이 반환됩니다.

---

## 개발 환경 설정

### 1. IDE 확장 프로그램 설치

**IntelliJ IDEA**
- Lombok Plugin (Settings → Plugins → "Lombok" 검색 후 설치)
- MyBatis Log Plugin (MyBatis SQL 로그 확인용)

**VS Code**
- Extension Pack for Java
- Lombok Annotations Support for VS Code
- MyBatis Log Plugin

### 2. Lombok 활성화

IntelliJ IDEA:
1. Settings → Build, Execution, Deployment → Compiler → Annotation Processors
2. "Enable annotation processing" 체크

### 3. 코드 스타일 설정

```bash
# Google Java Format 설치 (선택사항)
brew install google-java-format
```

---

## 테스트 실행

### 단위 테스트

```bash
# 모든 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests CrawlingControllerTest

# 테스트 결과 보고서
# 결과: build/reports/tests/test/index.html
```

### 통합 테스트

```bash
./gradlew integrationTest
```

---

## 실행 프로필

### 개발 프로필 (development)

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

`application-dev.yaml` 사용:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/dangdang_crawling_dev
```

### 프로덕션 프로필 (production)

```bash
./gradlew bootRun --args='--spring.profiles.active=prod'
```

`application-prod.yaml` 사용:
```yaml
spring:
  datasource:
    url: jdbc:mysql://prod-db-server:3306/dangdang_crawling
  jpa:
    hibernate:
      ddl-auto: validate
logging:
  level:
    root: WARN
```

---

## 로그 확인

### 콘솔 로그

```bash
tail -f logs/application.log
```

### IDE 콘솔

Run 창에서 실시간 로그 확인

---

## 포트 변경

기본 포트 8080을 변경하려면:

```bash
./gradlew bootRun --args='--server.port=9090'
```

또는 `application.yaml`에서:

```yaml
server:
  port: 9090
```

---

## 문제 해결

### 문제 1: "Cannot find symbol" 컴파일 오류

**원인**: Lombok이 정상 작동하지 않음

**해결책**:
```bash
# IDE 재시작
# 또는
./gradlew clean build
```

### 문제 2: MySQL 연결 오류

**원인**: 데이터베이스 서비스 미실행

**해결책**:
```bash
# macOS
brew services start mysql

# Linux
sudo systemctl start mysql

# Windows
# 서비스 관리자에서 MySQL80 서비스 시작
```

### 문제 3: "Access denied for user 'root'@'localhost'"

**원인**: MySQL 암호 오류

**해결책**:
```bash
# MySQL 암호 초기화
mysql -u root -p
# 또는
mysql -u root -p"password"
```

### 문제 4: 포트 8080이 이미 사용 중

**해결책**:
```bash
# macOS/Linux: 사용 중인 프로세스 확인
lsof -i :8080

# Windows: 사용 중인 프로세스 확인
netstat -ano | findstr :8080

# 프로세스 종료 후 다른 포트로 실행
./gradlew bootRun --args='--server.port=9090'
```

### 문제 5: Gradle 빌드 실패

**원인**: 의존성 다운로드 오류

**해결책**:
```bash
# Gradle 캐시 삭제
./gradlew clean --refresh-dependencies

# 다시 빌드
./gradlew build
```

---

## 다음 단계

1. [API 명세](./API.md) 확인
2. [데이터베이스 스키마](./DATABASE.md) 이해
3. [아키텍처](./ARCHITECTURE.md) 학습
4. 첫 번째 크롤링 작업 생성

---

## 지원

문제가 발생하면:
1. 이슈 생성: GitHub Issues
2. 로그 확인: `logs/application.log`
3. 콘솔 출력 확인
4. 문서 재검토


