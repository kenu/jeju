# 제주 데이터 허브 페이징 서비스

제주 데이터 허브 API를 활용한 데이터 조회 및 표시 웹 애플리케이션입니다. 이 서비스는 제주도의 다양한 데이터를 페이징 처리하여 보여주며, 사용자 수에 따른 색상 코딩과 모바일 환경에서의 반응형 디자인을 지원합니다.

## 주요 기능

- 제주 데이터 허브 API를 통한 데이터 조회
- 페이징 처리를 통한 대량 데이터 효율적 표시
- 사용자 수에 따른 색상 차별화 (낮음, 중간, 높음, 매우 높음)
- 모바일 환경에서의 반응형 디자인
  - 테이블 가로 스크롤
  - 중요하지 않은 컬럼 숨김
  - 폰트 크기 자동 조정

## 기술 스택

- **Backend**: Spring Boot
- **Frontend**: HTML, CSS, JavaScript
- **Template Engine**: Thymeleaf
- **Testing**: JUnit, MockMvc
- **API 통신**: RestTemplate

## 시작하기

### 필수 조건

- JDK 11 이상
- Maven 3.6 이상
- 제주 데이터 허브 API 키

### 환경 변수 설정

API 키를 보호하기 위해 환경 변수를 사용합니다:

```
JEJU_DATAHUB_API_URL=https://open.jejudatahub.net/api/proxy/bt1t10001baaa01ata06a1aD0011b6t1/YOUR_API_KEY
```

### 빌드 및 실행

```bash
# 프로젝트 클론
git clone https://github.com/kenu/jeju.git
cd jeju

# 빌드
./mvnw clean package

# 실행
./mvnw spring-boot:run
```

애플리케이션은 기본적으로 http://localhost:8080 에서 실행됩니다.

## 테스트

다음 명령어로 테스트를 실행할 수 있습니다:

```bash
./mvnw test
```

테스트는 다음 영역을 포함합니다:
- 기본 기능 테스트 (리다이렉트, 데이터 조회)
- 입력 유효성 검증 테스트 (페이지 크기, 날짜 형식)
- 오류 처리 테스트 (클라이언트 및 서버 오류)
- 사용자 요구사항 관련 테스트 (색상 코딩, 모바일 반응형)

## API 엔드포인트

- `GET /jeju`: 제주 데이터 페이지로 리다이렉트
- `GET /jeju/api/data`: 제주 데이터 허브 API 데이터 조회
  - 파라미터:
    - `page`: 페이지 번호 (기본값: 0)
    - `size`: 페이지 크기 (기본값: 10)
    - `startDate`: 시작 날짜 (기본값: 201601)
    - `endDate`: 종료 날짜 (기본값: 202201)

## 라이센스

이 프로젝트는 MIT 라이센스 하에 배포됩니다.

## 기여

이슈 제출 및 풀 리퀘스트를 통해 프로젝트에 기여할 수 있습니다.
