import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val pluginId: String by project
val pluginDisplayName: String by project
val pluginDescription: String by project
val pluginImplementationClass: String by project
val pluginDeclarationName: String by project
val pluginGroupPackageName: String by project
val pluginVersion: String by project

@Suppress(
    "UnstableApiUsage",
    "DSL_SCOPE_VIOLATION"
)
plugins {
    kotlin("jvm") version(libs.versions.kotlin)
    //alias(libs.plugins.detekt)
    `java-gradle-plugin`
    `maven-publish`
    jacoco
}

group = pluginGroupPackageName
version = pluginVersion

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testImplementation(kotlin("test"))
    compileOnly(gradleApi())
    implementation(libs.koin)
    implementation(libs.oshi)
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}

plugins.withType<JavaPlugin>().configureEach {
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        csv.required.set(true)
        html.required.set(true)
    }
}

publishing {
    publications {
        repositories {
            mavenLocal()
        }
    }
}

gradlePlugin {
    plugins {
        create(pluginDeclarationName) {
            id = pluginId
            displayName = pluginDisplayName
            description = pluginDescription
            implementationClass = pluginImplementationClass
        }
    }
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.BIN
}

tasks.register("publishToLocal") {
    doLast {
        exec {
            commandLine(
                "./gradlew",
                "build",
                "test",
                "publishToMavenLocal"
            ).args("--info", "--stacktrace")
        }
    }
}
