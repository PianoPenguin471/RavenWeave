package me.PianoPenguin471.command;

import club.maxstats.weave.loader.api.command.Command;
import keystrokesmod.client.main.Raven;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class TestCommand extends Command {
    public TestCommand() {
        super("test","t");
    }

    @Override
    public void handle(String[] args) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("This is a command!"));
    }
}