package me.PianoPenguin471;

import net.weavemc.loader.api.ModInitializer;
import net.weavemc.loader.api.command.CommandBus;
import net.weavemc.loader.api.event.EventBus;
import net.weavemc.loader.api.event.KeyboardEvent;
import net.weavemc.loader.api.event.ShutdownEvent;
import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.Module;
import me.PianoPenguin471.command.TestCommand;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class RavenWeave implements ModInitializer {
    @Override
    public void preInit() {
        System.out.println("Initializing ExampleMod!");

        Raven.init();

        CommandBus.register(new TestCommand());
        EventBus.subscribe(KeyboardEvent.class, (keyboardEvent) -> {
            for (Module module: Raven.moduleManager.getModules()) {
                module.keybind();
            }
        });

        EventBus.subscribe(ShutdownEvent.class, (shutdownEvent) -> {
            Raven.moduleManager.getModuleByName("Blink").disable();
        });

    }
}
