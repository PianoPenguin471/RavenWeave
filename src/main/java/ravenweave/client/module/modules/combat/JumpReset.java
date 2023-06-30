package ravenweave.client.module.modules.combat;

import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import ravenweave.client.event.impl.PacketEvent;
import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.DescriptionSetting;

public class JumpReset extends Module {

    public JumpReset() {
        super("JumpReset", ModuleCategory.combat);

        this.registerSetting(new DescriptionSetting("Auto Jump Reset. That's it."));
    }

    @Subscribe
    public void onPacket(PacketEvent e) {
        if (e.isIncoming()) {
            if (e.getPacket() instanceof S12PacketEntityVelocity) {
                if (((S12PacketEntityVelocity) e.getPacket()).getEntityID() == mc.thePlayer.getEntityId()) {
                    if(mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                    }
                }
            }
        }
    }

}
