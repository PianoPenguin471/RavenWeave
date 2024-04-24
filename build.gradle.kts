plugins {
    id("java")
    kotlin("jvm") version ("1.9.0")
    id("net.weavemc.gradle") version "1.0.0-PRE2"
}

group = "me.pianopenguin471"
version = "1.1.5-Beta"

weave {
    configure {
        name = "RavenWeave"
        modId = "ravenweave"
        entryPoints = listOf("me.pianopenguin471.RavenWeave")
        hooks = listOf(
            "ravenweave.client.hook.GoogleDemapper",
            "ravenweave.client.hook.EntityPlayerSPHook",
            "ravenweave.client.hook.EntityRendererHook",
            "ravenweave.client.hook.RendererLivingEntityHook",

            "ravenweave.client.hook.finder.Finder\$EntityRenderer\$getNightVisionBrightness",
            "ravenweave.client.hook.finder.Finder\$Entity\$callResetPositionToBB",

            "ravenweave.client.hook.event.ChatReceivedEventHook",
            "ravenweave.client.hook.event.ChatSentEventHook",
            "ravenweave.client.hook.event.EntityListEventAddHook",
            "ravenweave.client.hook.event.EntityListEventRemoveHook",
            "ravenweave.client.hook.event.GuiOpenEventHook",
            "ravenweave.client.hook.event.KeyboardEventHook",
            "ravenweave.client.hook.event.MouseEventHook",
            "ravenweave.client.hook.event.PlayerListEventHook",
            "ravenweave.client.hook.event.RenderGameOverlayHook",
            "ravenweave.client.hook.event.RenderHandEventHook",
            "ravenweave.client.hook.event.RenderLivingEventHook",
            "ravenweave.client.hook.event.RenderWorldEventHook",
            "ravenweave.client.hook.event.ServerConnectEventHook",
            "ravenweave.client.hook.event.ShutdownEventHook",
            "ravenweave.client.hook.event.StartGameEventHook",
            "ravenweave.client.hook.event.TickEventHook",
            "ravenweave.client.hook.event.WorldEventHook",
            "ravenweave.client.hook.event.PacketEventHook",
        )
        mixinConfigs = listOf("ravenweave.mixins.json")
        mcpMappings()
    }
    version("1.8.9")
}

repositories {
    maven("https://repo.weavemc.dev/releases")
    maven("https://repo.spongepowered.org/maven/")
    maven("https://jitpack.io")
}

dependencies {
    implementation("net.weavemc.api:common:1.0.0-PRE2")
    implementation("net.weavemc:internals:1.0.0-PRE2")
    compileOnly("org.spongepowered:mixin:0.8.5")
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
}

kotlin {
    jvmToolchain(8)
}
