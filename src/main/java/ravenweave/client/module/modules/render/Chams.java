package ravenweave.client.module.modules.render;

import net.weavemc.loader.api.event.RenderLivingEvent;
import net.weavemc.loader.api.event.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import ravenweave.client.module.Module;
import ravenweave.client.module.modules.world.AntiBot;
import ravenweave.client.module.setting.impl.DescriptionSetting;
import ravenweave.client.module.setting.impl.TickSetting;

public class Chams extends Module {
    public static TickSetting ignoreBots;
    public Chams() {
        super("Chams", ModuleCategory.render);
        this.registerSetting(new DescriptionSetting("Shows player through walls"));
        this.registerSetting(ignoreBots = new TickSetting("Hide bots", true));
    }

    @SubscribeEvent
    public void onPreLivingRender(RenderLivingEvent.Pre event) {
        if (event.getEntity() != mc.thePlayer) {
            GL11.glEnable(32823);
            GL11.glPolygonOffset(1.0F, -1100000.0F);
        }
    }

    @SubscribeEvent
    public void onPostLivingRender(RenderLivingEvent.Post event) {
        if (event.getEntity() != mc.thePlayer && (!ignoreBots.isToggled() || !AntiBot.bot(event.getEntity()))) {
            GL11.glDisable(32823);
            GL11.glPolygonOffset(1.0F, 1100000.0F);
        }
    }
}
