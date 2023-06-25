package me.pianopenguin471.command;

import net.weavemc.loader.api.command.Command;
import ravenweave.client.main.Raven;

public class GUICommand extends Command {
    public GUICommand() {
        super("clickgui","gui");
    }

    @Override
    public void handle(String[] args) {
        Raven.moduleManager.getModuleByName("Gui").toggle();
    }
}
