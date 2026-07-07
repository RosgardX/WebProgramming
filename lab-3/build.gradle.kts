plugins {
    java
    id("war")
}

group = "ru.lab"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    providedCompile("jakarta.platform:jakarta.jakartaee-api:10.0.0")

    implementation("org.primefaces:primefaces:13.0.6:jakarta")

    runtimeOnly("org.postgresql:postgresql:42.6.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    named<War>("war") {
        archiveBaseName.set("points-lab")
        archiveVersion.set(version.toString())
    }
}