package ravenweave.client.module.modules.render;

import ravenweave.client.module.Module;
import net.minecraft.potion.Potion;
import net.weavemc.loader.api.event.*;

public class AntiDebuff extends Module {
    public AntiDebuff() {
        super("AntiDebuff", ModuleCategory.render);
    }

    @SubscribeEvent
    public void onUpdate(Event e) {
        if (mc.thePlayer != null) {
            mc.thePlayer.removePotionEffectClient(Potion.blindness.getId());
        }
    }
}
