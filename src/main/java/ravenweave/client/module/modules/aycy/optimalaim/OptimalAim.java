package ravenweave.client.module.modules.aycy.optimalaim;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Timer;
import net.minecraft.util.Vec3;
import net.weavemc.loader.api.event.RenderLivingEvent;
import net.weavemc.loader.api.event.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import ravenweave.client.module.Module;
import ravenweave.client.module.modules.aycy.optimalaim.e.BoundingBoxWrapper;
import ravenweave.client.module.modules.aycy.optimalaim.f.PlayerPosWrapper;
import ravenweave.client.module.modules.client.Targets;
import ravenweave.client.module.setting.impl.RGBSetting;
import ravenweave.client.module.setting.impl.SliderSetting;

public class OptimalAim extends Module {
    public static RGBSetting cubeColor, outlineColor;
    public static PlayerPosWrapper ourPlayerWrapper, targetPlayerWrapper;
    public static SliderSetting radius;
    public OptimalAim() {
        super("OptimalAim", ModuleCategory.render);
        this.registerSetting(cubeColor = new RGBSetting("Cube color", 255, 0, 0));
        this.registerSetting(outlineColor = new RGBSetting("Outline color", 0, 255, 0));
        this.registerSetting(radius = new SliderSetting("Radius", 0.1, 0.01, 0.2, 0.01));
    }
    private EntityPlayer target;


    @SubscribeEvent()
    public void onRenderLiving(RenderLivingEvent a) {
        if (a.getEntity() instanceof EntityPlayer b) {
            this.target = Targets.getTarget();
            if (this.target != null && this.target == b) {
                ourPlayerWrapper = new PlayerPosWrapper(a, mc.thePlayer);
                targetPlayerWrapper = new PlayerPosWrapper(a, b);
                this.draw(ourPlayerWrapper.getBoundingBox(targetPlayerWrapper));
            }
        }
    }

    public static Vec3 getOptimalAim() {
        if (ourPlayerWrapper == null || targetPlayerWrapper == null) return null;
        BoundingBoxWrapper box = ourPlayerWrapper.getBoundingBox(targetPlayerWrapper);
        return box.getCenter();
    }

    private void draw(BoundingBoxWrapper a) {
        GlStateManager.pushMatrix();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(true);
        GlStateManager.disableLighting();
        GlStateManager.enableCull();

        //Cube
        GlStateManager.color((float) cubeColor.getRed() / 255, (float) cubeColor.getGreen() / 255, (float) cubeColor.getBlue() / 255);
        // I have no idea where this method is
        // c.drawBoundingBox(a);


        //Outline
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(2.0F);
        GlStateManager.color((float) outlineColor.getRed() / 255, (float) outlineColor.getGreen() / 255, (float) outlineColor.getBlue() / 255);
        RenderGlobal.drawSelectionBoundingBox(a);


        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
    }
}
