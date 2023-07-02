package ravenweave.client.module.modules.movement;

import com.google.common.eventbus.Subscribe;
import ravenweave.client.event.impl.TickEvent;
import ravenweave.client.main.Raven;
import ravenweave.client.module.Module;
import ravenweave.client.module.modules.combat.Reach;
import ravenweave.client.module.setting.impl.DescriptionSetting;
import ravenweave.client.module.setting.impl.SliderSetting;
import ravenweave.client.module.setting.impl.TickSetting;

public class KeepSprint extends Module {
    public static DescriptionSetting description;
    public static SliderSetting speed;
    public static TickSetting reduce;

    public KeepSprint() {
        super("KeepSprint", ModuleCategory.movement);
        this.registerSetting(description = new DescriptionSetting("Keep your sprint. (BROKEN)"));
        this.registerSetting(speed = new SliderSetting("Slow %", 40.0D, 0.0D, 100.0D, 1.0D));
        this.registerSetting(reduce = new TickSetting("Only reduce reach hits", false));
        // Readd sprint reset option
    }

    // This is still broken :(, but it's closer in the correct direction

    @Subscribe
    public void onTick(TickEvent e) { // Change this to an event based off attacking a player
        double dist;
        Module reach = Raven.moduleManager.getModuleByClazz(Reach.class);
        if (reduce.isToggled() && reach != null && reach.isEnabled() && !mc.thePlayer.capabilities.isCreativeMode) {
            dist = mc.objectMouseOver.hitVec.distanceTo(mc.getRenderViewEntity().getPositionEyes(1.0F));
            double val;
            if (dist > 3.0D) {
                val = (100.0D - (double)((float)speed.getInput())) / 100.0D;
            } else {
                val = 0.6D;
            }

            mc.thePlayer.motionX *= val;
            mc.thePlayer.motionZ *= val;
        } else {
            dist = (100.0D - (double)((float)speed.getInput())) / 100.0D;
            mc.thePlayer.motionX *= dist;
            mc.thePlayer.motionZ *= dist;
        }
    }
}
