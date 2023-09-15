package ravenweave.client.module.modules.movement;

import net.minecraft.network.play.server.S30PacketWindowItems;
import net.weavemc.loader.api.event.SubscribeEvent;
import ravenweave.client.event.impl.PacketEvent;
import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.DescriptionSetting;
import ravenweave.client.module.setting.impl.SliderSetting;
import ravenweave.client.module.setting.impl.TickSetting;

public class NoSlow extends Module {
    public static SliderSetting speed;
    public static TickSetting noReset;

    public NoSlow() {
        super("NoSlow", ModuleCategory.movement);
        this.registerSetting(new DescriptionSetting("Default is 80% motion reduction."));
        this.registerSetting(new DescriptionSetting("'No Reset' bypasses select Anticheats."));
        this.registerSetting(speed = new SliderSetting("Slow %", 80.0D, 0.0D, 80.0D, 1.0D));
        this.registerSetting(noReset = new TickSetting("No Reset", false));
    }

    @SubscribeEvent
    public void onPacket(PacketEvent e) {
        if (mc.thePlayer != null && mc.theWorld != null) {
            if (noReset.isToggled()) {
                if (e.getPacket() instanceof S30PacketWindowItems) {
                    if (mc.thePlayer.isUsingItem()) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

}
