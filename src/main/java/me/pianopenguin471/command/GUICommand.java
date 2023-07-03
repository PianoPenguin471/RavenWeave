package me.pianopenguin471.command;

import net.minecraft.util.ChatComponentText;
import net.weavemc.loader.api.command.Command;
import org.lwjgl.input.Keyboard;
import ravenweave.client.main.Raven;
import ravenweave.client.module.Module;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.client.Minecraft;

public class GUICommand extends Command {
    private static final String UsageMessage = EnumChatFormatting.WHITE + "[" + EnumChatFormatting.LIGHT_PURPLE + "Raven" + EnumChatFormatting.WHITE + "] " + EnumChatFormatting.RED + "/gui <key>";
    private static final String InvalidKeyMessage = EnumChatFormatting.WHITE + "[" + EnumChatFormatting.LIGHT_PURPLE + "Raven" + EnumChatFormatting.WHITE + "] " + EnumChatFormatting.RED + "Invalid key.";
    private static final String SuccessMessage = EnumChatFormatting.WHITE + "[" + EnumChatFormatting.LIGHT_PURPLE + "Raven" + EnumChatFormatting.WHITE + "] " + EnumChatFormatting.WHITE + "GUI Bind set to " + EnumChatFormatting.GREEN + "%s" + EnumChatFormatting.WHITE + ".";

    public GUICommand() {
        super("clickgui", "gui", "raven");
    }

    public int getStringAsKeycode(String keyString) {
        if (keyString.equalsIgnoreCase("none")) {
            return 0;
        } else {
            return Keyboard.getKeyIndex(keyString.toUpperCase());
        }
    }

    public String getKeycodeAsString(int keyCode) {
        if (keyCode == 0) {
            return "none";
        } else {
            return Keyboard.getKeyName(keyCode);
        }
    }

    @Override
    public void handle(String[] args) {
        Module guiModule = Raven.moduleManager.getModuleByName("Gui");

        if (args.length != 1) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(UsageMessage));
            return;
        }

        String keyString = args[0];
        int keyCode = getStringAsKeycode(keyString);
        keyString = getKeycodeAsString(keyCode);

        if (keyString.equalsIgnoreCase("none")) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(InvalidKeyMessage));
            return;
        }

        guiModule.setBind(keyCode);
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(String.format(SuccessMessage, keyString)));
    }
}
