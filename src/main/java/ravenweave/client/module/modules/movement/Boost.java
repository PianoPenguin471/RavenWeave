package ravenweave.client.module.modules.movement;

import com.google.common.eventbus.Subscribe;
import ravenweave.client.event.impl.TickEvent;
import ravenweave.client.main.Raven;
import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.DescriptionSetting;
import ravenweave.client.module.setting.impl.SliderSetting;
import ravenweave.client.utils.Utils;

public class Boost extends Module {
    public static DescriptionSetting description;
    public static SliderSetting timerSpeed;
    public static SliderSetting timerTime;
    private int initialTicks;
    private boolean isTimerEnabled;

    public Boost() {
        super("Boost", ModuleCategory.movement);
        this.registerSetting(description = new DescriptionSetting("Timer boost"));
        this.registerSetting(timerSpeed = new SliderSetting("Multiplier", 2.0D, 1.0D, 3.0D, 0.05D));
        this.registerSetting(timerTime = new SliderSetting("Time (ticks)", 15.0D, 1.0D, 80.0D, 1.0D));
    }

    public void onEnable() {
        Module timer = Raven.moduleManager.getModuleByClazz(Timer.class);
        if (timer != null && timer.isEnabled()) {
            this.isTimerEnabled = true;
            timer.disable();
        }

    }

    public void onDisable() {
        this.initialTicks = 0;
        if (Utils.Client.getTimer().timerSpeed != 1.0F) {
            Utils.Client.resetTimer();
        }

        if (this.isTimerEnabled) {
            Module timer = Raven.moduleManager.getModuleByClazz(Timer.class);
            if (timer != null)
                timer.enable();
        }

        this.isTimerEnabled = false;
    }

    @Subscribe
    public void onTick(TickEvent e) {
        if (this.initialTicks == 0) {
            this.initialTicks = mc.thePlayer.ticksExisted;
        }

        Utils.Client.getTimer().timerSpeed = (float) timerSpeed.getInput();
        if ((double) this.initialTicks == (double) mc.thePlayer.ticksExisted - timerTime.getInput()) {
            Utils.Client.resetTimer();
            this.disable();
        }

    }
}