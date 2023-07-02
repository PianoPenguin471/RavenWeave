package me.pianopenguin471;

import net.weavemc.loader.api.ModInitializer;
import net.weavemc.loader.api.command.CommandBus;
import net.weavemc.loader.api.event.EventBus;
import net.weavemc.loader.api.event.KeyboardEvent;
import net.weavemc.loader.api.event.ShutdownEvent;
import ravenweave.client.main.Raven;
import ravenweave.client.module.Module;
import me.pianopenguin471.command.RavenCommand;
import net.weavemc.loader.api.event.StartGameEvent;

public class RavenWeave implements ModInitializer {
    @Override
    public void preInit() {
        System.out.println("Initializing RavenWeave!");

        CommandBus.register(new RavenCommand());
        EventBus.subscribe(KeyboardEvent.class, (keyboardEvent) -> {
            for (Module module: Raven.moduleManager.getModules()) {
                module.keybind();
            }
        });

        EventBus.subscribe(ShutdownEvent.class, (shutdownEvent) -> {
            Raven.moduleManager.getModuleByName("Blink").disable();
        });

        EventBus.subscribe(StartGameEvent.Post.class, startGameEvent -> {
            Raven.init();
        });
    }
}
