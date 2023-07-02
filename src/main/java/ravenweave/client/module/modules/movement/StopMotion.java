package ravenweave.client.module.modules.movement;

import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.DescriptionSetting;
import ravenweave.client.module.setting.impl.TickSetting;
import ravenweave.client.utils.Utils;

public class StopMotion extends Module {
    public static DescriptionSetting description;
    public static TickSetting stopX, stopY, stopZ;

    public StopMotion() {
        super("StopMotion", ModuleCategory.movement);
        this.registerSetting(description = new DescriptionSetting("Stops motion"));
        this.registerSetting(stopX = new TickSetting("Stop X", true));
        this.registerSetting(stopY = new TickSetting("Stop Y", true));
        this.registerSetting(stopZ = new TickSetting("Stop Z", true));
    }

    public void onEnable() {
        if (!Utils.Player.isPlayerInGame()) {
            this.disable();
            return;
        }

        if (stopX.isToggled())
            mc.thePlayer.motionX = 0;

        if (stopY.isToggled())
            mc.thePlayer.motionY = 0;

        if (stopZ.isToggled())
            mc.thePlayer.motionZ = 0;

        this.disable();
    }
}
