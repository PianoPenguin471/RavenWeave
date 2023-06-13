package keystrokesmod.client.module.modules.render;

import net.weavemc.loader.api.event.RenderGameOverlayEvent;
import net.weavemc.loader.api.event.SubscribeEvent;

import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.modules.client.Targets;
import keystrokesmod.client.module.setting.impl.RGBSetting;
import keystrokesmod.client.module.setting.impl.SliderSetting;
import keystrokesmod.client.utils.RenderUtils;
import me.PianoPenguin471.events.AttackEntityEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static net.minecraft.client.gui.Gui.drawScaledCustomSizeModalRect;

public class TargetHUD extends Module {
    // further decrease dimensions for a smaller HUD
    public static final int HEAD_X = 4, HEAD_Y = 4, WIDTH = 100, HEIGHT = 50;
    public int screenHeight, screenWidth;
    public FontRenderer fr;
    public static RGBSetting borderColor, mainColor;
    public static SliderSetting xSetting, ySetting, NAME_OFFSET_X, NAME_OFFSET_Y;
    ScaledResolution sr;

    public TargetHUD() {
        super("Target HUD", ModuleCategory.beta);
        this.registerSetting(borderColor = new RGBSetting("Border", 89, 89, 89));
        this.registerSetting(mainColor = new RGBSetting("Main Color", 39, 39, 39));
        this.registerSettings(
                xSetting = new SliderSetting("X", 5, 0, 1000, 1),
                ySetting = new SliderSetting("Y", 5, 0, 1000, 1),
                NAME_OFFSET_X = new SliderSetting("Name Offset X", 5, 0, 200, 2),
                NAME_OFFSET_Y = new SliderSetting("Name Offset Y", 5, 0, 200, 2)
        );
        sr = new ScaledResolution(Minecraft.getMinecraft());
        screenHeight = sr.getScaledHeight();
        screenWidth = sr.getScaledWidth();
        fr = mc.fontRendererObj;
    }

    @SubscribeEvent
    public void onForgeEvent(AttackEntityEvent e) {
        if (!this.enabled) return;
        if (!(e.target instanceof AbstractClientPlayer)) return;
        EntityPlayer ep = (EntityPlayer) e.target;
    }


    @SubscribeEvent
    public void onRender2d(RenderGameOverlayEvent.Pre e) {
        drawTargetHUD((int) xSetting.getInput(), (int) ySetting.getInput());
    }

    public static void drawTargetHUD(int x, int y) {
        // Get target
        EntityPlayer thatGuy = Targets.getTarget();

        // Filter target
        if (!(thatGuy instanceof EntityOtherPlayerMP)) return;
        EntityOtherPlayerMP target = (EntityOtherPlayerMP) thatGuy;

        // Draw Border
        RenderUtils.drawBorderedRoundedRect(x, y, x + WIDTH, y + HEIGHT, 8, 2, borderColor.getRGB(), mainColor.getRGB());

        // Draw Face
        mc.getTextureManager().bindTexture(target.getLocationSkin());
        drawScaledCustomSizeModalRect(x + HEAD_X, y + HEAD_Y, 8.0f, 8.0f, 8, 8, 12, 12, 32.0f, 32.0f);
        if (target.isWearing(EnumPlayerModelParts.HAT)) {
            drawScaledCustomSizeModalRect(x + HEAD_X, y + HEAD_Y, 40.0f, 8.0f, 8, 8, 12, 12, 32.0f, 32.0f);
        }
        GlStateManager.bindTexture(0);
        GL11.glScalef(1.0f, 1.0f, 1);

        int currentY = y;

        currentY += (NAME_OFFSET_Y.getInput());

        // Draw Name
        mc.fontRendererObj.drawString("Name: " + target.getName(), (int) ((x + NAME_OFFSET_X.getInput())), currentY, 0xff00ffff);

        // Draw Distance
        currentY += (NAME_OFFSET_Y.getInput() + mc.fontRendererObj.FONT_HEIGHT);
        mc.fontRendererObj.drawString("Distance: " + String.format("%.2f", target.getDistanceToEntity(mc.thePlayer)) + " blocks", (x + HEAD_X), currentY, 0x00ff00ff);

        // Draw Health Bar
        currentY += mc.fontRendererObj.FONT_HEIGHT + HEAD_Y;
        RenderUtils.drawBorderedRoundedRect((x + HEAD_X), currentY, (x + WIDTH - HEAD_X), (y + HEIGHT - HEAD_Y), 2, 4, Color.GRAY.getRGB(), Color.RED.getRGB());

        GL11.glScalef(1,1,1);
    }
}
