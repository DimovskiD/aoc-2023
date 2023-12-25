plugins {
    kotlin("jvm") version "1.9.20"
}

sourceSets {
    main {
        kotlin.srcDir("src")
    }
}
repositories {
    mavenLocal()
    mavenCentral()
}

tasks {
    wrapper {
        gradleVersion = "8.5"
    }
}

dependencies {
    implementation("org.jgrapht:jgrapht:1.5.2")
    implementation("org.jgrapht:jgrapht-core:1.5.2")
    implementation("tools.aqua:z3-turnkey:4.12.2.1")
}
