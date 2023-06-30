package ravenweave.client.module.modules.movement;

import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.server.S30PacketWindowItems;
import ravenweave.client.event.impl.PacketEvent;
import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.DescriptionSetting;
import ravenweave.client.module.setting.impl.SliderSetting;
import ravenweave.client.module.setting.impl.TickSetting;

public class NoSlow extends Module {
    public static DescriptionSetting a;
    public static DescriptionSetting c;
    public static SliderSetting b;
    public static TickSetting noReset;

    public NoSlow() {
        super("NoSlow", ModuleCategory.movement);
        this.registerSetting(a = new DescriptionSetting("Default is 80% motion reduction."));
        this.registerSetting(c = new DescriptionSetting("Use 'No Reset' on Hypixel."));
        this.registerSetting(b = new SliderSetting("Slow %", 80.0D, 0.0D, 80.0D, 1.0D));
        this.registerSetting(noReset = new TickSetting("No Reset", false));
    }

    @Subscribe
    public void onPacket(PacketEvent e) {
        if (noReset.isToggled()) {
            if (e.getPacket() instanceof S30PacketWindowItems) {
                if (mc.thePlayer.isUsingItem()) {
                    e.cancel();
                }
            }
        }
    }

}
