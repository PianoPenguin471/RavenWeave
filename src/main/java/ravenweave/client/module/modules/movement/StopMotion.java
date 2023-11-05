package ravenweave.client.module.modules.movement;

import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.TickSetting;
import ravenweave.client.utils.Utils;

public class StopMotion extends Module {
    public static TickSetting x, y, z;

    public StopMotion() {
        super("Stop Motion", ModuleCategory.movement);
        this.registerSetting(x = new TickSetting("Stop X", true));
        this.registerSetting(y = new TickSetting("Stop Y", true));
        this.registerSetting(z = new TickSetting("Stop Z", true));
    }

    public void onEnable() {
        if (!Utils.Player.isPlayerInGame()) {
            this.disable();
            return;
        }

        if (x.isToggled())
            mc.thePlayer.motionX = 0;

        if (y.isToggled())
            mc.thePlayer.motionY = 0;

        if (z.isToggled())
            mc.thePlayer.motionZ = 0;

        this.disable();
    }
}
