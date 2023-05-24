plugins {
    java
    id("com.github.weave-mc.weave") version "8b70bcc707"
}

group = "me.PianoPenguin471"
version = "1.1.2"

minecraft.version("1.8.9")

repositories {
    maven("https://jitpack.io")
    maven("https://repo.spongepowered.org/maven/")
}

dependencies {
    compileOnly("com.github.weave-mc:weave-loader:70bd82faa6")

    compileOnly("org.spongepowered:mixin:0.8.5")
}

tasks.compileJava {
    options.release.set(11)
}