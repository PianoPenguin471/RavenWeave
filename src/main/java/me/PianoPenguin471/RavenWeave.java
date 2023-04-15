package me.PianoPenguin471;

import club.maxstats.weave.loader.api.ModInitializer;
import club.maxstats.weave.loader.api.command.CommandBus;
import club.maxstats.weave.loader.api.event.EventBus;
import club.maxstats.weave.loader.api.event.KeyboardEvent;
import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.Module;
import me.PianoPenguin471.command.TestCommand;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class RavenWeave implements ModInitializer {
    @Override
    public void init() {
        System.out.println("Initializing ExampleMod!");

        Raven.init();

        CommandBus.register(new TestCommand());
        EventBus.subscribe(KeyboardEvent.class, (keyboardEvent) -> {
            for (Module module: Raven.moduleManager.getModules()) {
                module.keybind();
            }
        });

    }

    public static Color getColor() {
        return Color.GREEN;
    }
}
