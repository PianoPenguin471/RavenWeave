package ravenweave.client.module.modules.movement;

import com.google.common.eventbus.Subscribe;
import io.netty.util.internal.ThreadLocalRandom;
import ravenweave.client.event.impl.TickEvent;
import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.DescriptionSetting;
import ravenweave.client.module.setting.impl.SliderSetting;
import ravenweave.client.module.setting.impl.TickSetting;
import ravenweave.client.utils.Utils;
import org.lwjgl.input.Keyboard;

public class AutoHeader extends Module {
    public static DescriptionSetting desc;
    public static TickSetting cancelDuringShift, onlyWhenHoldingSpacebar;
    public static SliderSetting pbs;
    private double startWait;

    public AutoHeader() {
        super("AutoHeadHitter", ModuleCategory.movement);
        this.registerSetting(desc = new DescriptionSetting("Spams spacebar when under blocks"));
        this.registerSetting(cancelDuringShift = new TickSetting("Cancel if snkeaing", true));
        this.registerSetting(onlyWhenHoldingSpacebar = new TickSetting("Only when holding jump", true));
        this.registerSetting(pbs = new SliderSetting("Jump Presses per second", 12, 1, 20, 1));

    }

    @Override
    public void onEnable() {
        startWait = System.currentTimeMillis();
        super.onEnable();
    }

    @Subscribe
    public void onTick(TickEvent e) {
        if (!Utils.Player.isPlayerInGame() || mc.currentScreen != null)
            return;

        if (cancelDuringShift.isToggled() && mc.thePlayer.isSneaking())
            return;

        if (onlyWhenHoldingSpacebar.isToggled()) {
            if (!Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
                return;
            }
        }

        if (Utils.Player.playerUnderBlock() && mc.thePlayer.onGround) {
            if (startWait + (1000 / ThreadLocalRandom.current().nextDouble(pbs.getInput() - 0.543543,
                    pbs.getInput() + 1.32748923)) < System.currentTimeMillis()) {
                mc.thePlayer.jump();
                startWait = System.currentTimeMillis();
            }
        }

    }
}
