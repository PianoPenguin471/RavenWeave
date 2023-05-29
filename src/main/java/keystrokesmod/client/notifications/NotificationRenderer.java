package keystrokesmod.client.notifications;

import net.weavemc.loader.api.event.RenderGameOverlayEvent;
import net.weavemc.loader.api.event.SubscribeEvent;

import keystrokesmod.client.config.ConfigManager;
import keystrokesmod.client.main.ClientConfig;
import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.modules.client.GuiModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class NotificationRenderer {
    public static final NotificationRenderer notificationRenderer = new NotificationRenderer();
    private static Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent e) {
        if (GuiModule.notifications())
            NotificationManager.render();
    }

    public static void moduleStateChanged(Module m) {
        if (!GuiModule.notifications() || mc.currentScreen != null || ConfigManager.applyingConfig
                || ClientConfig.applyingConfig)
            return;

        if (!m.getClass().equals(Gui.class)) {
            String s = m.isEnabled() ? "enabled" : "disabled";
            NotificationManager
                    .show(new Notification(NotificationType.INFO, "Module " + s, m.getName() + " has been " + s, 1));
        }
    }
}