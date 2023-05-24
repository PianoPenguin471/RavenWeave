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

import static net.minecraft.client.gui.Gui.drawScaledCustomSizeModalRect;

public class TargetHUD extends Module {
    public static final int HEAD_X = 5, HEAD_Y = 5, WIDTH = 200, NAME_OFFSET_X = 40, NAME_OFFSET_Y = 15;
    public int screenHeight, screenWidth;
    public FontRenderer fr;
    private static EntityOtherPlayerMP target;
    public static RGBSetting borderColor;
    public static SliderSetting xSetting, ySetting, nameOffsetX, nameOffsetY;
    ScaledResolution sr;

    public TargetHUD() {
        super("Target HUD", /*ModuleCategory.render*/ModuleCategory.beta);
        this.registerSetting(borderColor = new RGBSetting("Border", 49, 203, 113));
        this.registerSettings(
                xSetting    = new SliderSetting("X",             5, 0,  10, 1),
                ySetting    = new SliderSetting("Y",             5, 0,  10, 1),
                nameOffsetX = new SliderSetting("Name Offset X", 5, 0, 400, 5),
                nameOffsetY = new SliderSetting("Name Offset Y", 5, 0, 400, 5)
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
    public void onRender2d(RenderGameOverlayEvent e) {
        drawTargetHUD((int) xSetting.getInput(), (int) ySetting.getInput());
    }

    public static void drawTargetHUD(int x, int y) {
        // Get target
        EntityPlayer thatGuy = Targets.getTarget();

        // Filter target
        if (!(thatGuy instanceof EntityOtherPlayerMP)) return;
        target = (EntityOtherPlayerMP) thatGuy;


        // Draw Border
        RenderUtils.drawRoundedOutline(x, y, x + WIDTH, y + 75, 15, 5, borderColor.getRGB());

        // Draw Face
        mc.getTextureManager().bindTexture(target.getLocationSkin());
        drawScaledCustomSizeModalRect(x+HEAD_X, y+HEAD_Y, 8.0f, 8.0f, 8, 8, 24, 24, 64.0f, 64.0f);
        if (target.isWearing(EnumPlayerModelParts.HAT)) {
            drawScaledCustomSizeModalRect(x+HEAD_X, y+HEAD_Y, 40.0f, 8.0f, 8, 8, 24, 24, 64.0f, 64.0f);
        }
        GlStateManager.bindTexture(0);

        // Draw Name
        mc.fontRendererObj.drawString(target.getName(), x + NAME_OFFSET_X, y + NAME_OFFSET_Y, 0xff00ffff);
    }
}
