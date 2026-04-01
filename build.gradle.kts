plugins {
    java
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
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

    // Tsid
    implementation("io.hypersistence:hypersistence-utils-hibernate-63:3.15.2")

    // H2 (test 전용)
    testImplementation("com.h2database:h2")

    // Lombok & DevTools & Config metadata
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
    outputs.dir(snippetsDir)
    finalizedBy(tasks.asciidoctor)
}

// Asciidoctor HTML 생성
tasks.asciidoctor {
    dependsOn(tasks.test)
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
    dependsOn(copyRestDocs)
    from(tasks.asciidoctor.get().outputDir) {
        into("static/docs")
    }
}

tasks.bootRun {
    dependsOn(copyRestDocs)
}

// 전체 build 수행 시 copyRestDocs가 완료되어야 함을 명시
tasks.build {
    dependsOn(copyRestDocs)
}
