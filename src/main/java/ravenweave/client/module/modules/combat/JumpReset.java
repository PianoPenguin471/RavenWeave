package ravenweave.client.module.modules.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.weavemc.loader.api.event.SubscribeEvent;
import ravenweave.client.event.impl.PacketEvent;
import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.DescriptionSetting;
import ravenweave.client.module.setting.impl.SliderSetting;

public class JumpReset extends Module {

    public static SliderSetting chance;

    public JumpReset() {
        super("JumpReset", ModuleCategory.combat);

        this.registerSetting(new DescriptionSetting("Auto Jump Reset. That's it."));
        this.registerSetting(chance = new SliderSetting("Chance", 100.0D, 0.0D, 100.0D, 1.0D));
    }

    @SubscribeEvent
    public void onPacket(PacketEvent e) {
        if (!e.isIncoming()) return;
        if (!(e.getPacket() instanceof S12PacketEntityVelocity)) return;
        if (chance.getInput() != 100.0D) {
            double ch = Math.random() * 100;
            if (ch >= chance.getInput()) {
                return;
            }
        }
        Entity entity = mc.theWorld.getEntityByID(((S12PacketEntityVelocity) e.getPacket()).getEntityID());
        if (entity == mc.thePlayer) {
            int key = Minecraft.getMinecraft().gameSettings.keyBindJump.getKeyCode();
            KeyBinding.setKeyBindState(key, true);
            KeyBinding.onTick(key);
            javax.swing.Timer timer = new javax.swing.Timer(20, actionevent -> KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false));
            timer.setRepeats(false);
            timer.start();
        }

    }

}
