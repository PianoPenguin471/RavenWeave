package ravenweave.client.module.modules.player;

import com.google.common.eventbus.Subscribe;
import ravenweave.client.event.impl.TickEvent;
import ravenweave.client.main.Raven;
import ravenweave.client.module.Module;
import ravenweave.client.module.modules.movement.Fly;
import ravenweave.client.module.setting.impl.DescriptionSetting;
import ravenweave.client.module.setting.impl.SliderSetting;
import ravenweave.client.module.setting.impl.TickSetting;

public class FallSpeed extends Module {
    public static DescriptionSetting dc;
    public static SliderSetting a;
    public static TickSetting b;

    public FallSpeed() {
        super("FallSpeed", ModuleCategory.player);
        this.registerSetting(new DescriptionSetting("Change fall speed"));
        this.registerSetting(dc = new DescriptionSetting("Vanilla max: 3.92"));
        this.registerSetting(a = new SliderSetting("Motion", 5.0D, 0.0D, 8.0D, 0.1D));
        this.registerSetting(b = new TickSetting("Disable XZ motion", true));
    }

    @Subscribe
    public void onTick(TickEvent e) {
        if ((double) mc.thePlayer.fallDistance >= 2.5D) {
            Module fly = Raven.moduleManager.getModuleByClazz(Fly.class);
            Module noFall = Raven.moduleManager.getModuleByClazz(NoFall.class);

            if ((fly != null && fly.isEnabled()) || (noFall != null && noFall.isEnabled())) {
                return;
            }

            if (mc.thePlayer.capabilities.isCreativeMode || mc.thePlayer.capabilities.isFlying) {
                return;
            }

            if (mc.thePlayer.isOnLadder() || mc.thePlayer.isInWater() || mc.thePlayer.isInLava()) {
                return;
            }

            mc.thePlayer.motionY = -a.getInput();
            if (b.isToggled()) {
                mc.thePlayer.motionX = mc.thePlayer.motionZ = 0.0D;
            }
        }

    }
}
