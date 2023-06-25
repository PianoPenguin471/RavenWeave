package ravenweave.client.module.modules.movement;

import com.google.common.eventbus.Subscribe;
import ravenweave.client.event.impl.TickEvent;
import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.TickSetting;
import ravenweave.client.utils.Utils;
import net.minecraft.client.settings.KeyBinding;

public class Sprint extends Module {
    public static TickSetting multiDir, ignoreBlindness;

    public Sprint() {
        super("Sprint", ModuleCategory.movement);
        this.registerSetting(multiDir = new TickSetting("All Directions", false));
        this.registerSetting(ignoreBlindness = new TickSetting("Ignore Blindness", false));
    }

    @Subscribe
    public void p(TickEvent e) {
        if (Utils.Player.isPlayerInGame() && mc.inGameHasFocus) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
        }
    }

}
