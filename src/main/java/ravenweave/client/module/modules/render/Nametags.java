package ravenweave.client.module.modules.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.weavemc.loader.api.event.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import ravenweave.client.event.impl.RenderLabelEvent;
import ravenweave.client.module.Module;
import ravenweave.client.module.modules.world.AntiBot;
import ravenweave.client.module.setting.impl.DescriptionSetting;
import ravenweave.client.module.setting.impl.SliderSetting;
import ravenweave.client.module.setting.impl.TickSetting;
import ravenweave.client.utils.Utils;

public class Nametags extends Module {
    public static SliderSetting rect;
    public static TickSetting scale, showHealth, shadow;

    public Nametags() {
        super("Nametags", ModuleCategory.render);
        this.registerSetting(new DescriptionSetting("Looks weird sometimes, sorry"));
        this.registerSetting(rect = new SliderSetting("Rect Opacity", 25.0D, 0.0D, 100.0D, 1.0D));
        this.registerSetting(scale = new TickSetting("Scale (wacky)", true));
        this.registerSetting(showHealth = new TickSetting("Show health", true));
        this.registerSetting(shadow = new TickSetting("Shadow", false));
    }

    @SubscribeEvent
    public void onRenderLabel(RenderLabelEvent event) {
        if(event.getTarget() instanceof EntityPlayer en) {
            event.setCancelled(true);

            if (AntiBot.bot(en) || en.getDisplayName().getUnformattedText().isEmpty()) return;

            String str = en.getDisplayName().getFormattedText();
            if (showHealth.isToggled()) {
                double r = en.getHealth() / en.getMaxHealth();
                String health = (r < 0.3D ? "\u00A7c" : (r < 0.5D ? "\u00A76" : (r < 0.7D ? "\u00A7e" : "\u00A7a")))
                        + Utils.Java.round(en.getHealth(), 1);
                str = str + " " + health;
            }

            GlStateManager.pushMatrix();

            GlStateManager.translate((float) event.getX(), (float) event.getY() + en.height + 0.5F, (float) event.getZ());

            GL11.glNormal3f(0.0F, 1.0F, 0.0F);

            GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);

            float disScale = (mc.thePlayer.getDistanceToEntity(en) / 4F / 150F) * 2F;

            if (scale.isToggled() && mc.thePlayer.getDistanceToEntity(en) > 6F) {
                GL11.glScalef((float) (-disScale * 1.077), (float) (-disScale * 1.077), (float) (disScale * 1.077));
            } else {
                GlStateManager.scale(-0.02666667F, -0.02666667F, 0.02666667F);
            }

            GlStateManager.depthMask(false);
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();

            drawLabelRect(str);

            mc.fontRendererObj.drawString(str, (float) -mc.fontRendererObj.getStringWidth(str) / 2, 0, -1, shadow.isToggled());

            GlStateManager.depthMask(true);
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();

            GlStateManager.popMatrix();
        }
    }

    public void drawLabelRect(String str) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        int j = mc.fontRendererObj.getStringWidth(str) / 2;
        float opacity = (float) (rect.getInput()/ 100);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(-j - 1, -1, 0.0D).color(0.0F, 0.0F, 0.0F, opacity).endVertex();
        worldrenderer.pos(-j - 1, 8, 0.0D).color(0.0F, 0.0F, 0.0F, opacity).endVertex();
        worldrenderer.pos(j + 1, 8, 0.0D).color(0.0F, 0.0F, 0.0F, opacity).endVertex();
        worldrenderer.pos(j + 1, -1, 0.0D).color(0.0F, 0.0F, 0.0F, opacity).endVertex();
        tessellator.draw();
    }
}
