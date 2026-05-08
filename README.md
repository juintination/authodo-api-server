# Authodo

Clean Architecture(Hexagonal) 학습을 위한 간단한 인증(Auth) + Todo 프로젝트

---

## 헥사고날 아키텍처

```
authodo
├── domain          # 핵심 비즈니스 로직 (외부 의존성 없음)
├── application     # 유스케이스 (UseCase 인터페이스 + Service 구현)
└── adapter
    ├── in/web      # Inbound — REST Controller, Security Filter
    └── out         # Outbound — JPA Repository, Redis Repository
```

### 레이어 책임

**Domain**

- 도메인 엔티티와 비즈니스 로직 보유
- 외부 기술에 대한 Port 인터페이스 정의
- 어떤 프레임워크에도 의존하지 않음

**Application**

- 각 기능을 UseCase 인터페이스로 분리
- Service가 UseCase를 구현하며 Domain Port에 의존
- 입출력 데이터를 Command / Result DTO로 명시적으로 표현

**Adapter (In)**

- Controller는 UseCase 인터페이스에만 의존
- JWT, Security 등 기술은 Port 구현체로 캡슐화

**Adapter (Out)**

- Domain Port를 구현하며 JPA Entity ↔ Domain Entity 변환 담당
- 저장소 기술(JPA, Redis)은 Adapter 내부에 은닉

### Port & Adapter 구성

| Port                         | 위치          | 기술    |
|------------------------------|-------------|-------|
| `TodoRepositoryPort`         | domain      | JPA   |
| `UserRepositoryPort`         | domain      | JPA   |
| `RefreshTokenRepositoryPort` | domain      | Redis |
| `TokenProviderPort`          | application | JWT   |

### 의존성 방향

```
Controller → UseCase ← Service → Port ← Adapter
```

- 화살표는 의존 방향
- Service는 Port 인터페이스에 의존하고, Adapter가 Port를 구현함으로써 의존성이 역전됨
- Domain과 Application은 구체적인 기술(JPA, Redis, JWT)을 알지 못함

---

## API 문서 생성

```
./gradlew generateDocs
```

테스트 실행 → REST Docs 스니펫 생성 → `index.adoc` 자동 생성 → HTML 변환 → OpenAPI 3.0 YAML 생성까지 한 번에 수행

생성된 문서는 애플리케이션 실행 후 아래 경로에서 확인할 수 있다.

| 경로                       | 내용           |
|--------------------------|--------------|
| `/docs/index.html`       | REST Docs 문서 |
| `/swagger-ui/index.html` | Swagger UI   |

### 문서 생성 원리

두 가지 플러그인을 조합해 REST Docs HTML과 Swagger UI를 동시에 제공한다.

- `org.asciidoctor.jvm.convert` — AsciiDoc → HTML 변환
- `com.epages.restdocs-api-spec` — REST Docs 스니펫 → OpenAPI 3.0 YAML 변환

**파이프라인**

```
test → generateDocsIndex → asciidoctor → copyRestDocs → openapi3 → copyOpenApiSpec
```

**단계별 설명**

1. **test**
    - MockMvc 기반 테스트 실행 시 `spring-restdocs-mockmvc`와 `restdocs-api-spec-mockmvc`가 요청/응답을 캡처해
      `build/generated-snippets/{테스트명}/` 아래에 `.adoc` 스니펫 파일을 생성한다.
2. **generateDocsIndex**
    - `build/generated-snippets`를 스캔해 `index.adoc`을 자동 생성한다. 디렉터리명의 첫 번째 `-` 이전 단어를 섹션 그룹으로 사용하며
      (예: `auth-login` → `Auth API`), 존재하는 스니펫(`http-request`, `request-fields`, `http-response` 등)만 include 지시어로 삽입한다.
3. **asciidoctor**
    - 생성된 `index.adoc`을 HTML로 변환한다. `snippets` 속성에 스니펫 경로를 주입해 include 지시어가 실제 파일을 참조하도록 한다.
4. **copyRestDocs**
    - 변환된 HTML을 `build/resources/main/static/docs/`로 복사해 앱 실행 시 `/docs/index.html`로 서빙한다.
5. **openapi3**
    - 동일한 스니펫을 읽어 OpenAPI 3.0 YAML(`build/api-spec/openapi3.yaml`)을 생성한다.
6. **copyOpenApiSpec**
    - 생성된 YAML을 `build/resources/main/static/docs/`로 복사한다. `springdoc-openapi-starter-webmvc-ui`가 이 파일을 읽어
      `/swagger-ui/index.html`에서 Swagger UI를 제공한다.
