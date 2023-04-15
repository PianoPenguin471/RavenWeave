package me.PianoPenguin471.listener;

import club.maxstats.weave.loader.api.event.RenderGameOverlayEvent;
import club.maxstats.weave.loader.api.event.SubscribeEvent;
import net.minecraft.client.renderer.GlStateManager;

import static org.lwjgl.opengl.GL11.*;

public class RenderGameOverlayListener {
    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        /*
        GlStateManager.disableTexture2D();

        GlStateManager.color(1f, 0f, 0f);

        glBegin(GL_QUADS);
        glVertex2f(0, 0);
        glVertex2f(0, 50);
        glVertex2f(50, 50);
        glVertex2f(50, 0);
        glEnd();

        GlStateManager.color(1f, 1f, 1f);
        GlStateManager.enableTexture2D();
        */
    }
}
