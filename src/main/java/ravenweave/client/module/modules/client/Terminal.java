package ravenweave.client.module.modules.client;

import com.google.gson.JsonObject;
import net.weavemc.loader.api.event.SubscribeEvent;
import ravenweave.client.clickgui.raven.ClickGui;
import ravenweave.client.event.impl.GameLoopEvent;
import ravenweave.client.main.Raven;
import ravenweave.client.module.Module;
import ravenweave.client.module.setting.Setting;
import ravenweave.client.module.setting.impl.SliderSetting;
import ravenweave.client.utils.Timer;
import ravenweave.client.utils.Utils;

public class Terminal extends Module {
    public static Timer animation;
    public static SliderSetting opacity;

    public Terminal() {
        super("Terminal", ModuleCategory.client);
        this.registerSetting(opacity = new SliderSetting("Terminal background opacity", 100, 0, 255, 1));
    }

    public void onEnable() {
        Raven.clickGui.terminal.show();
        (animation = new Timer(500.0F)).start();
    }

    @SubscribeEvent
    public void onGameLoop(GameLoopEvent e) {
        if (Utils.Player.isPlayerInGame() && mc.currentScreen instanceof ClickGui && Raven.clickGui.terminal.hidden())
            Raven.clickGui.terminal.show();
    }

    public void onDisable() {
        Raven.clickGui.terminal.hide();

        if (animation != null) {
            animation.start();
        }
    }

    @Override
    public void applyConfigFromJson(JsonObject data) {
        try {
            this.keycode = data.get("keycode").getAsInt();
            JsonObject settingsData = data.get("settings").getAsJsonObject();
            for (Setting setting : getSettings()) {
                if (settingsData.has(setting.getName())) {
                    setting.applyConfigFromJson(settingsData.get(setting.getName()).getAsJsonObject());
                }
            }
        } catch (NullPointerException ignored) {

        }
    }

    @Override
    public void resetToDefaults() {
        this.keycode = defualtKeyCode;

        for (Setting setting : this.settings) {
            setting.resetToDefaults();
        }
    }
}
