package keystrokesmod.client.module.modules.render;

import club.maxstats.weave.loader.api.event.RenderLivingEvent;
import club.maxstats.weave.loader.api.event.SubscribeEvent;
import keystrokesmod.client.module.Module;
import org.lwjgl.opengl.GL11;

public class Chams extends Module {
    public Chams() {
        super("Chams", ModuleCategory.render);
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
        if (event.getEntity() != mc.thePlayer) {
            GL11.glDisable(32823);
            GL11.glPolygonOffset(1.0F, 1100000.0F);
        }
    }
}
