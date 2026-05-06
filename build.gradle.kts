plugins {
    java
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
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
val generatedAsciidocDir = layout.buildDirectory.dir("generated-asciidoc")

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

val snippetIncludes = listOf(
    "http-request",
    "request-fields",
    "path-parameters",
    "query-parameters",
    "http-response",
    "response-fields"
)

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
    finalizedBy("generateDocsIndex")
}

afterEvaluate {
    tasks.named("openapi3") { dependsOn(tasks.test); group = null }
    tasks.named("openapi") { group = null; enabled = false }
    tasks.named("postman") { group = null; enabled = false }
}

val copyOpenApiSpec by tasks.registering(Copy::class) {
    dependsOn("openapi3")
    from("build/api-spec/openapi3.yaml")
    into(layout.buildDirectory.dir("resources/main/static/docs"))
}

// 문서 전체 생성 진입점 (Gradle 창에서 이 태스크 하나만 실행하면 됨)
val generateDocs by tasks.registering {
    group = "documentation"
    description = "테스트 실행 → index.adoc 생성 → HTML 변환 → static/docs 복사 → OpenAPI 스펙 생성"
    dependsOn("copyRestDocs", "copyOpenApiSpec")
}

// snippets 디렉터리를 스캔해 index.adoc 자동 생성
val generateDocsIndex by tasks.registering {
    dependsOn(tasks.test)
    outputs.dir(generatedAsciidocDir)
    finalizedBy(tasks.asciidoctor)

    doLast {
        val outputDir = generatedAsciidocDir.get().asFile
        outputDir.mkdirs()

        val groups = (snippetsDir.listFiles() ?: emptyArray())
            .filter { it.isDirectory }
            .groupBy { it.name.substringBefore("-") }
            .toSortedMap()

        val content = buildString {
            appendLine("= Authodo API 문서")
            appendLine(":toc: left")
            appendLine(":toclevels: 2")
            appendLine(":sectnums:")
            appendLine()

            groups.forEach { (prefix, dirs) ->
                appendLine("== ${prefix.replaceFirstChar { it.uppercase() }} API")
                appendLine()

                dirs.sortedBy { it.name }.forEach { dir ->
                    appendLine("---")
                    appendLine()
                    appendLine("=== ${dir.name}")
                    appendLine()

                    snippetIncludes
                        .filter { dir.resolve("$it.adoc").exists() }
                        .forEach { snippet ->
                            appendLine("include::{snippets}/${dir.name}/$snippet.adoc[]")
                        }
                    appendLine()
                }
            }
        }

        File(outputDir, "index.adoc").writeText(content)
    }
}

// Asciidoctor HTML 생성
tasks.asciidoctor {
    dependsOn(generateDocsIndex)
    sourceDir(generatedAsciidocDir.get().asFile)
    inputs.dir(snippetsDir)
    attributes(mapOf("snippets" to snippetsDir))
    baseDirFollowsSourceDir()
    finalizedBy("copyRestDocs")
}

// 생성된 HTML을 정적 리소스 위치로 복사
val copyRestDocs by tasks.registering(Copy::class) {
    dependsOn(tasks.asciidoctor)
    from(tasks.asciidoctor.get().outputDir)
    into(layout.buildDirectory.dir("resources/main/static/docs"))
}

// 빌드 및 실행 시 문서가 포함되도록 보장
tasks.bootJar {
    dependsOn(copyRestDocs, "copyOpenApiSpec")
    from(tasks.asciidoctor.get().outputDir) {
        into("static/docs")
    }
}

tasks.bootRun {
    dependsOn(copyRestDocs, "copyOpenApiSpec")
}

// 전체 build 수행 시 문서 태스크가 완료되어야 함을 명시
tasks.build {
    dependsOn(copyRestDocs, "copyOpenApiSpec")
}
