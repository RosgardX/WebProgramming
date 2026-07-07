plugins {
    java
    war
}

group = "org.example"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")
    annotationProcessor("jakarta.servlet:jakarta.servlet-api:6.0.0")

    implementation("org.glassfish.web:jakarta.servlet.jsp.jstl:3.0.1")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}