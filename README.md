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
