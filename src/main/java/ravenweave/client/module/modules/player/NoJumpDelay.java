package ravenweave.client.module.modules.player;

import net.weavemc.loader.api.event.SubscribeEvent;
import net.weavemc.loader.api.event.TickEvent;
import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.DescriptionSetting;
import ravenweave.client.utils.Utils;

public class NoJumpDelay extends Module {
    public NoJumpDelay() {
        super("NoJumpDelay", ModuleCategory.player);
        this.registerSetting(new DescriptionSetting("No delay between jumps"));
    }

    @SubscribeEvent
    public void onTick(TickEvent e) {
        if (Utils.Player.isPlayerInGame() && mc.inGameHasFocus) {
            mc.thePlayer.jumpTicks = 0;
        }
    }
}
