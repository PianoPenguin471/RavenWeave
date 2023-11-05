package ravenweave.client.module.modules.combat;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.weavemc.loader.api.event.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import ravenweave.client.event.PacketEvent;
import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.DescriptionSetting;
import ravenweave.client.module.setting.impl.DoubleSliderSetting;
import ravenweave.client.module.setting.impl.SliderSetting;

import java.util.Random;

public class JumpReset extends Module {

    public static DoubleSliderSetting delay;
    public static SliderSetting chance;

    public JumpReset() {
        super("JumpReset", ModuleCategory.combat);
        this.registerSetting(new DescriptionSetting("Auto Jump Reset. That's it."));
        this.registerSetting(delay = new DoubleSliderSetting("Jump Delay", 10.0D, 20.0D, 0.0D, 40.0D, 1.0D));
        this.registerSetting(chance = new SliderSetting("Chance", 100.0D, 0.0D, 100.0D, 1.0D));
    }

    public Random rand = new Random();

    @SubscribeEvent
    public void onPacket(PacketEvent e) {
        if (e.getPacket() instanceof S12PacketEntityVelocity) {
            if (chance.getInput() != 100.0D) {
                double ch = Math.random() * 100;
                if (ch >= chance.getInput()) {
                    return;
                }
            }

            Entity entity = mc.theWorld.getEntityByID(((S12PacketEntityVelocity) e.getPacket()).getEntityID());
            int key = mc.gameSettings.keyBindJump.getKeyCode();
            if (entity == mc.thePlayer && mc.thePlayer.onGround && !Keyboard.isKeyDown(key)) {
                KeyBinding.setKeyBindState(key, true);
                KeyBinding.onTick(key);
                javax.swing.Timer timer = new javax.swing.Timer(rand.nextInt((int)delay.getInputMin(), (int)delay.getInputMax()), actionevent -> KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false));
                timer.setRepeats(false);
                timer.start();
            }
        }
    }

}
