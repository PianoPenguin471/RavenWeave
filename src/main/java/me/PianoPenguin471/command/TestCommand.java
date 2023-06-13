package me.PianoPenguin471.command;

import net.weavemc.loader.api.command.Command;
import keystrokesmod.client.main.Raven;

public class TestCommand extends Command {
    public TestCommand() {
        super("clickgui","gui", "test");
    }

    @Override
    public void handle(String[] args) {
        Raven.moduleManager.getModuleByName("Gui").enable();
    }
}
