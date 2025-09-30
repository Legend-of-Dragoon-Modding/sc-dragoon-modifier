import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

plugins {
  id("java")
  id("java-library")
  id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "dragoon-modifier"
version = "2.0-SNAPSHOT"

java {
  sourceCompatibility = JavaVersion.VERSION_25
  targetCompatibility = JavaVersion.VERSION_25
}

javafx {
  modules("javafx.controls", "javafx.fxml")
}

// This is so it picks up new builds on jitpack
configurations.all {
  resolutionStrategy.cacheChangingModulesFor(0, "seconds")
}

repositories {
  mavenCentral()
  mavenLocal() // Uncomment to use mavenLocal version of LoD engine
  maven { url = uri("https://central.sonatype.com/repository/maven-snapshots/") }
  maven { url = uri("https://jitpack.io") }
}

dependencies {
  implementation("legend:lod:snapshot") // Uncomment to use mavenLocal version of LoD engine (also comment out next line)
//  implementation("com.github.Legend-of-Dragoon-Modding:Legend-of-Dragoon-Java:main-SNAPSHOT")
  implementation("com.opencsv:opencsv:5.9")
  implementation("org.fusesource.jansi:jansi:2.4.1")
  implementation("org.apache.logging.log4j:log4j-api:2.24.3")
  implementation("org.apache.logging.log4j:log4j-core:2.24.3")
  implementation("com.google.code.findbugs:jsr305:3.0.2")
  implementation("com.github.JnCrMx:discord-game-sdk4j:v1.0.0")
  api("org.legendofdragoon:mod-loader:4.2.1")
  api("org.legendofdragoon:script-recompiler:0.5.6")
}

sourceSets {
  main {
    java {
      srcDirs("src/main/java")
      exclude(".gradle", "build", "files")
    }
  }
}

buildscript {
  repositories {
    gradlePluginPortal()
  }
}

apply(plugin = "java")
apply(plugin = "org.openjfx.javafxplugin")

tasks.jar {
  exclude("*.jar")
}
