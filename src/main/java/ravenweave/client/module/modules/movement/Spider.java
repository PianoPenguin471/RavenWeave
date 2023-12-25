package ravenweave.client.module.modules.movement;

import net.weavemc.loader.api.event.SubscribeEvent;
import net.weavemc.loader.api.event.TickEvent;
import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.DescriptionSetting;
import ravenweave.client.module.setting.impl.SliderSetting;
import ravenweave.client.utils.Utils;

public class Spider extends Module {
    public static SliderSetting speed;
    public Spider() {
        super("Spider", ModuleCategory.movement);
        this.registerSetting(new DescriptionSetting("Only for Vanilla/Karhu/Verus"));
        this.registerSetting(speed = new SliderSetting("Speed", 0.2, 0.1, 3.0D, 0.01D));
    }

    @SubscribeEvent
    public void onTick(TickEvent e) {
        if (!Utils.Player.isPlayerInGame() || !mc.thePlayer.isCollidedHorizontally) return;
        mc.thePlayer.motionY = speed.getInput();
    }
}
