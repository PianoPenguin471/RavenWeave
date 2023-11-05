package ravenweave.client.module.modules.movement;

import net.weavemc.loader.api.event.SubscribeEvent;
import ravenweave.client.event.HitSlowDownEvent;
import ravenweave.client.Raven;
import ravenweave.client.module.Module;
import ravenweave.client.module.modules.combat.Reach;
import ravenweave.client.module.setting.impl.DescriptionSetting;
import ravenweave.client.module.setting.impl.SliderSetting;
import ravenweave.client.module.setting.impl.TickSetting;

public class KeepSprint extends Module {
    public static SliderSetting speed;
    public static TickSetting reduce, sprint;

    public KeepSprint() {
        super("KeepSprint", ModuleCategory.movement);
        this.registerSetting(new DescriptionSetting("Default is 60% retained speed"));
        this.registerSetting(new DescriptionSetting("and stopping sprint."));
        this.registerSetting(speed = new SliderSetting("Retained speed", 60.0D, 0.0D, 100.0D, 1.0D));
        this.registerSetting(reduce = new TickSetting("Only reduce reach hits", false));
        this.registerSetting(sprint = new TickSetting("Stop Sprint", true));
    }

    @SubscribeEvent
    public void onHitSlowDown(HitSlowDownEvent e) {
        double dist;
        Module reach = Raven.moduleManager.getModuleByClazz(Reach.class);
        if (reduce.isToggled() && reach != null && reach.isEnabled() && !mc.thePlayer.capabilities.isCreativeMode) {
            dist = mc.objectMouseOver.hitVec.distanceTo(mc.getRenderViewEntity().getPositionEyes(1.0F));
            double val;
            if (dist > 3.0D) {
                val = (double) ((float) speed.getInput()) / 100.0D;
            } else {
                val = 0.6D;
            }

            e.setSlowDown(val);
        } else {
            double val;
            val = (double) ((float) speed.getInput()) / 100.0D;
            e.setSlowDown(val);
        }
        if (sprint.isToggled()) {
            e.setSprinting(false);
        }
    }
}
