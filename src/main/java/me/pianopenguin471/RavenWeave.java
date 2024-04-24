package me.pianopenguin471;

import me.pianopenguin471.command.CommandBus;
import me.pianopenguin471.command.impl.GUICommand;
import net.weavemc.api.ModInitializer;
import net.weavemc.api.event.EventBus;
import net.weavemc.api.event.SubscribeEvent;
import ravenweave.client.Raven;
import ravenweave.client.event.ShutdownEvent;
import ravenweave.client.module.Module;

import java.lang.instrument.Instrumentation;

public class RavenWeave implements ModInitializer {
    @Override
    public void preInit(Instrumentation instrumentation) {
        CommandBus.register(new GUICommand());
        EventBus.subscribe(this);
    }

    @SubscribeEvent
    public void onKeyPress(ravenweave.client.event.KeyboardEvent e) {
        for (Module module: Raven.moduleManager.getModules()) {
            module.keybind();
        }
    }

    @SubscribeEvent
    public void onGameStart(ravenweave.client.event.StartGameEvent.Post e) {
        Raven.init();
    }

    @SubscribeEvent
    public void onShutdown(ShutdownEvent e) {
        Raven.moduleManager.getModuleByName("Blink").disable();
    }
}
