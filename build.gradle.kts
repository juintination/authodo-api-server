plugins {
    java
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.epages.restdocs-api-spec") version "0.19.4"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Clean Architecture Study"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

val snippetsDir = file("build/generated-snippets")

openapi3 {
    setServer("http://localhost:8080")
    title = "Authodo API"
    description = "Authodo API 문서"
    version = "1.0.0"
    format = "yaml"
    outputFileNamePrefix = "openapi3"
    outputDirectory = "build/api-spec"
    snippetsDirectory = "build/generated-snippets"
}

dependencies {
    // Web & Validation
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Security
    implementation("org.springframework.boot:spring-boot-starter-security")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    // JPA & MySQL
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.mysql:mysql-connector-j")

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // Tsid
    implementation("io.hypersistence:hypersistence-utils-hibernate-63:3.15.2")

    // H2 (test 전용)
    testImplementation("com.h2database:h2")

    // Lombok & DevTools & Config metadata
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // Swagger UI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.3")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("com.epages:restdocs-api-spec-mockmvc:0.19.4")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
    outputs.dir(snippetsDir)
}

afterEvaluate {
    tasks.named("openapi3") { dependsOn(tasks.test); group = null }
    tasks.named("openapi") { group = null; enabled = false }
    tasks.named("postman") { group = null; enabled = false }
}

// 생성된 openapi3.yaml을 static/docs 아래 배치해 Swagger UI가 읽을 수 있도록 복사
val copyOpenApiSpec by tasks.registering(Copy::class) {
    dependsOn("openapi3")
    from("build/api-spec/openapi3.yaml")
    into(layout.buildDirectory.dir("resources/main/static/docs"))
}

// 문서 생성 진입점: test → openapi3 → copyOpenApiSpec
val generateDocs by tasks.registering {
    group = "documentation"
    description = "테스트 실행 → OpenAPI 3.0 YAML 생성 → static/docs 복사"
    dependsOn("copyOpenApiSpec")
}

// bootJar / bootRun / build 수행 시 Swagger UI 문서가 자동으로 포함되도록 보장
tasks.bootJar {
    dependsOn("copyOpenApiSpec")
}

tasks.bootRun {
    dependsOn("copyOpenApiSpec")
}

tasks.build {
    dependsOn("copyOpenApiSpec")
}
