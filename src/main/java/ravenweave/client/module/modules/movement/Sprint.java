package ravenweave.client.module.modules.movement;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.settings.KeyBinding;
import ravenweave.client.event.impl.TickEvent;
import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.DescriptionSetting;
import ravenweave.client.module.setting.impl.TickSetting;
import ravenweave.client.utils.Utils;

public class Sprint extends Module {
    public static TickSetting multiDir, ignoreBlindness;
    public static DescriptionSetting description;

    public Sprint() {
        super("Sprint", ModuleCategory.movement);
        this.registerSetting(description = new DescriptionSetting("Change sprint"));
        this.registerSetting(multiDir = new TickSetting("All Directions", false));
        this.registerSetting(ignoreBlindness = new TickSetting("Ignore Blindness", false));
    }

    // Confused? The rest of the code is in MixinEntityPlayerSP.java

    @Subscribe
    public void onTick(TickEvent e) {
        if (Utils.Player.isPlayerInGame() && mc.inGameHasFocus) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
        }
    }

}
