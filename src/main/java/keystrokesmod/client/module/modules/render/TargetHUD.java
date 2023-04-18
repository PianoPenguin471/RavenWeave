package keystrokesmod.client.module.modules.render;

import club.maxstats.weave.loader.api.event.RenderGameOverlayEvent;
import club.maxstats.weave.loader.api.event.SubscribeEvent;

import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.setting.impl.TickSetting;
import me.PianoPenguin471.events.AttackEntityEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;

public class TargetHUD extends Module {
    public TickSetting editPosition;
    public int height, width;
    public FontRenderer fr;
    private AbstractClientPlayer target;
    ScaledResolution sr;

    public TargetHUD() {
        super("Target HUD", ModuleCategory.render);
        sr = new ScaledResolution(Minecraft.getMinecraft());
        height = sr.getScaledHeight();
        width = sr.getScaledWidth();
        fr = mc.fontRendererObj;
    }

    @SubscribeEvent
    public void onForgeEvent(AttackEntityEvent e) {
        if (!this.enabled) return;
        System.out.println(e.target instanceof AbstractClientPlayer);
        System.out.println(e.target);
        EntityPlayer ep = (EntityPlayer) e.target;
    }


    @SubscribeEvent
    public void onRender2d(RenderGameOverlayEvent e) {
        if (!this.enabled) return;
        /*try {
            ResourceLocation skin = AbstractClientPlayer.getLocationSkin();
            Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
            GL11.glEnable(GL11.GL_BLEND);
            Gui.drawScaledCustomSizeModalRect(0, 0, 50, 50, 50, 50, 50, 50, 50, 50);
            GL11.glDisable(GL11.GL_BLEND);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } */
    }
}
