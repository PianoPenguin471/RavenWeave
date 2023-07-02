package me.pianopenguin471.command;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.weavemc.loader.api.command.Command;
import ravenweave.client.main.Raven;

public class RavenCommand extends Command {
    public RavenCommand() {
        super("raven");
    }

    @Override
    public void handle(String[] args) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(String.valueOf(Raven.moduleManager.getModuleByName("Gui").getKeycode())));
    }
}
