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

extra["snippetsDir"] = file("build/generated-snippets")

dependencies {
	// Web & Validation
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")

	/*
	// Security
	implementation("org.springframework.boot:spring-boot-starter-security")

	// JWT
	implementation("io.jsonwebtoken:jjwt-api:0.12.5")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.5")
	*/

	// JPA & MySQL
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	runtimeOnly("com.mysql:mysql-connector-j")

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
}

tasks.test {
	outputs.dir(project.extra["snippetsDir"]!!)
}

extra["snippetsDir"] = file("build/generated-snippets")
val snippetsDir = project.extra["snippetsDir"] as File

tasks.asciidoctor {
	inputs.dir(snippetsDir)
	attributes(mapOf("snippets" to snippetsDir))
	baseDirFollowsSourceDir()
	dependsOn(tasks.test)
}

// 생성된 HTML을 정적 리소스 위치로 복사
val copyRestDocs by tasks.registering(Copy::class) {
	dependsOn(tasks.asciidoctor)
	from(tasks.asciidoctor.get().outputDir)
	into(layout.buildDirectory.dir("resources/main/static/docs"))
}

tasks.named("copyRestDocs").configure {
	mustRunAfter(tasks.processResources)
}

tasks.named("resolveMainClassName").configure {
	dependsOn(tasks.named("copyRestDocs"))
}

tasks.bootRun { dependsOn(tasks.named("copyRestDocs")) }
tasks.bootJar {
	dependsOn(tasks.named("copyRestDocs"))
	from(tasks.asciidoctor.get().outputDir) { into("static/docs") }
}
