import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.2.6.RELEASE"
	id("io.spring.dependency-management") version "1.0.9.RELEASE"
	kotlin("jvm") version "1.4.32"
	kotlin("plugin.spring") version "1.4.32"
	id("org.liquibase.gradle") version "2.0.2"
}

group = "eu.alkismavridis"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
	jcenter()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	// DB
	implementation("org.liquibase:liquibase-core:3.8.0")
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("com.h2database:h2:1.4.200")

	// GRAPHQL
	implementation("com.graphql-java-kickstart:graphql-spring-boot-starter:7.0.1")
	implementation("com.graphql-java-kickstart:graphql-java-tools:6.0.2")
	implementation("com.graphql-java:graphiql-spring-boot-starter:5.0.2")
	implementation("com.graphql-java:graphql-java-extended-scalars:1.0")

	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}


}

apply(plugin = "org.liquibase.gradle")


tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}
