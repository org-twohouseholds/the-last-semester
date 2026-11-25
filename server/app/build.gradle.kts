plugins {
    id("java-library")
    application
}
application {
    mainClass.set("com.extracreditstudios.thelastsemester.server.Server")
}
dependencies {
    implementation(project(":shared"))
    implementation("io.javalin:javalin:6.7.0")
    implementation("org.slf4j:slf4j-simple:2.0.16")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    implementation("com.mysql:mysql-connector-j:9.7.0")
    implementation("com.zaxxer:HikariCP:6.3.0")
    implementation("de.mkammerer:argon2-jvm:2.11")
    testImplementation(platform("org.junit:junit-bom:6.1.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("com.h2database:h2:2.5.252")
}

tasks.test {
    useJUnitPlatform()
}
