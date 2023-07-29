package ravenweave.client.module.modules;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.weavemc.loader.api.event.RenderGameOverlayEvent;
import net.weavemc.loader.api.event.SubscribeEvent;
import ravenweave.client.main.Raven;
import ravenweave.client.module.Module;
import ravenweave.client.module.modules.client.FakeHud;
import ravenweave.client.module.setting.Setting;
import ravenweave.client.module.setting.impl.ComboSetting;
import ravenweave.client.module.setting.impl.DescriptionSetting;
import ravenweave.client.module.setting.impl.SliderSetting;
import ravenweave.client.module.setting.impl.TickSetting;
import ravenweave.client.utils.Utils;
import ravenweave.client.utils.font.FontUtil;

import java.awt.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HUD extends Module {
    public static TickSetting editPosition, dropShadow, logo;
    public static ComboSetting<lmv> logoMode;
    public static ComboSetting<ColourModes> colorMode;
    public static SliderSetting logoScaleh, logoScalew;
    public static DescriptionSetting description, logoDesc1;
    private static int hudX = 5;
    private static int hudY = 70;
    private double logoHeight;
    public static boolean e;

    private InputStream inputStream;
    private ResourceLocation ravenLogo;

    public static Utils.HUD.PositionMode positionMode;
    public static boolean showedError;
    public static final String HUDX_prefix = "HUDX~ ";
    public static final String HUDY_prefix = "HUDY~ ";

    public enum lmv {
        l1, l2, l3, l4, l5, l6, l7, CD, ravenweave
    }

    public HUD() {
        super("HUD", ModuleCategory.render);
        this.registerSetting(description = new DescriptionSetting(("Show a list of modules.")));
        this.registerSetting(editPosition = new TickSetting("Edit position", false));
        this.registerSetting(dropShadow = new TickSetting("Drop shadow", true));
        //this.registerSetting(logo = new TickSetting("Logo", true));
        this.registerSetting(colorMode = new ComboSetting("Value: ", ColourModes.RAVEN));
        //this.registerSetting(logoScaleh = new SliderSetting("Logo Scale height ", 1, 0, 10, 0.01));
        //this.registerSetting(logoScalew = new SliderSetting("Logo Scale width ", 2, 0, 10, 0.01));
        //this.registerSetting(logoMode = new ComboSetting("Logo Mode:", lmv.l7));
        //this.registerSetting(logoDesc1 = new DescriptionSetting("cd logomode put an image logo.png"));
        //this.registerSetting(logoDesc1 = new DescriptionSetting("in the keystrokes folder"));
        showedError = false;
        showInHud = false;
    }

    private void setUpLogo() {
        ravenLogo = new ResourceLocation("keystrokesmod", "logohud/ravenweave.png");
    }

    @Override
	public void postApplyConfig() {
        setUpLogo();

    }

    @Override
	public void guiButtonToggled(Setting b) {
        if (b == logoMode)
            setUpLogo();
        else if (b == editPosition) {
            editPosition.disable();
            mc.displayGuiScreen(new EditHudPositionScreen());
        }
    }

    public boolean logoLoaded() {
        return (ravenLogo != null) && logo.isToggled();
    }

    @Override
	public void onEnable() {
        Raven.moduleManager.sort();
    }

    @SubscribeEvent
    public void onRender2D(RenderGameOverlayEvent.Pre ev) {
        if (Utils.Player.isPlayerInGame()) {
            if ((mc.currentScreen != null) || mc.gameSettings.showDebugInfo)
                return;
            boolean fakeHUDEnabled = Raven.moduleManager.getModuleByName("Fake Hud").isEnabled();
            if (!e) {
                ScaledResolution sr = new ScaledResolution(mc);
                positionMode = Utils.HUD.getPostitionMode(hudX, hudY, sr.getScaledWidth(), sr.getScaledHeight());
                if ((positionMode == Utils.HUD.PositionMode.UPLEFT) || (positionMode == Utils.HUD.PositionMode.UPRIGHT)) {
                    if (!fakeHUDEnabled)
                        Raven.moduleManager.sortShortLong();
                    else
                        FakeHud.sortShortLong();
                } else if ((positionMode == Utils.HUD.PositionMode.DOWNLEFT)
                        || (positionMode == Utils.HUD.PositionMode.DOWNRIGHT))
                    if (!fakeHUDEnabled)
                        Raven.moduleManager.sortLongShort();
                    else
                        FakeHud.sortLongShort();
                e = true;
            }
            int margin = 2;
            int y = hudY;
            int del = 0;

            List<Module> en = fakeHUDEnabled ? FakeHud.getModules() : new ArrayList<>(Raven.moduleManager.getModules());
            if (en.isEmpty())
                return;

            int textBoxWidth = Raven.moduleManager.getLongestActiveModule(mc.fontRendererObj);
            int textBoxHeight = Raven.moduleManager.getBoxHeight(mc.fontRendererObj, margin);

            if (hudX < 0)
                hudX = margin;
            if (hudY < 0)
                hudY = margin;

            if ((hudX + textBoxWidth) > (mc.displayWidth / 2))
                hudX = (mc.displayWidth / 2) - textBoxWidth - margin;

            if ((hudY + textBoxHeight) > (mc.displayHeight / 2))
                hudY = (mc.displayHeight / 2) - textBoxHeight;

            //drawLogo(textBoxWidth);
            //y += logoHeight;
            for (Module m : en) {
                if (m.isEnabled() && m.showInHud()) {
                    if ((positionMode == Utils.HUD.PositionMode.DOWNRIGHT) || (positionMode == Utils.HUD.PositionMode.UPRIGHT)) {
                        switch (colorMode.getMode()) {
                            case RAVEN:
                                mc.fontRendererObj.drawString(m.getName(),
                                        (float) hudX + (textBoxWidth - mc.fontRendererObj.getStringWidth(m.getName())),
                                        (float) y, Utils.Client.rainbowDraw(2L, del), dropShadow.isToggled());
                                y += mc.fontRendererObj.FONT_HEIGHT + margin;
                                del -= 120;
                                break;
                            case RAVEN2:
                                mc.fontRendererObj.drawString(m.getName(),
                                        (float) hudX + (textBoxWidth - mc.fontRendererObj.getStringWidth(m.getName())),
                                        (float) y, Utils.Client.rainbowDraw(2L, del), dropShadow.isToggled());
                                y += mc.fontRendererObj.FONT_HEIGHT + margin;
                                del -= 10;
                                break;
                            case ASTOLFO:
                                mc.fontRendererObj.drawString(m.getName(),
                                        (float) hudX + (textBoxWidth - mc.fontRendererObj.getStringWidth(m.getName())),
                                        (float) y, Utils.Client.astolfoColorsDraw(10, 14), dropShadow.isToggled());
                                y += mc.fontRendererObj.FONT_HEIGHT + margin;
                                del -= 120;
                                break;
                            case ASTOLFO2:
                                mc.fontRendererObj.drawString(m.getName(),
                                        (float) hudX + (textBoxWidth - mc.fontRendererObj.getStringWidth(m.getName())),
                                        (float) y, Utils.Client.astolfoColorsDraw(10, del), dropShadow.isToggled());
                                y += mc.fontRendererObj.FONT_HEIGHT + margin;
                                del -= 120;
                                break;
                            case ASTOLFO3:
                                mc.fontRendererObj.drawString(m.getName(),
                                        (float) hudX + (textBoxWidth - mc.fontRendererObj.getStringWidth(m.getName())),
                                        (float) y, Utils.Client.astolfoColorsDraw(10, del), dropShadow.isToggled());
                                y += mc.fontRendererObj.FONT_HEIGHT + margin;
                                del -= 10;
                                break;
                            case KV:
                                FontUtil.two.drawString(m.getName(),
                                        (double) hudX + (textBoxWidth - mc.fontRendererObj.getStringWidth(m.getName())), y,
                                        Utils.Client.customDraw(del), dropShadow.isToggled(), 10);
                                y += mc.fontRendererObj.FONT_HEIGHT + margin;
                                del -= 10;
                                break;
                        }
                    } else if (colorMode.getMode() == ColourModes.RAVEN) {
                        mc.fontRendererObj.drawString(m.getName(), (float) hudX, (float) y,
                                Utils.Client.rainbowDraw(2L, del), dropShadow.isToggled());
                        y += mc.fontRendererObj.FONT_HEIGHT + margin;
                        del -= 120;
                    } else if (colorMode.getMode() == ColourModes.RAVEN2) {
                        mc.fontRendererObj.drawString(m.getName(), (float) hudX, (float) y,
                                Utils.Client.rainbowDraw(2L, del), dropShadow.isToggled());
                        y += mc.fontRendererObj.FONT_HEIGHT + margin;
                        del -= 10;
                    } else if (colorMode.getMode() == ColourModes.ASTOLFO) {
                        mc.fontRendererObj.drawString(m.getName(), (float) hudX, (float) y,
                                Utils.Client.astolfoColorsDraw(10, 14), dropShadow.isToggled());
                        y += mc.fontRendererObj.FONT_HEIGHT + margin;
                        del -= 120;
                    } else if (colorMode.getMode() == ColourModes.ASTOLFO2) {
                        mc.fontRendererObj.drawString(m.getName(), (float) hudX, (float) y,
                                Utils.Client.astolfoColorsDraw(10, del), dropShadow.isToggled());
                        y += mc.fontRendererObj.FONT_HEIGHT + margin;
                        del -= 120;
                    } else if (colorMode.getMode() == ColourModes.ASTOLFO3) {
                        mc.fontRendererObj.drawString(m.getName(), (float) hudX, (float) y,
                                Utils.Client.astolfoColorsDraw(10, del), dropShadow.isToggled());
                        y += mc.fontRendererObj.FONT_HEIGHT + margin;
                        del -= 10;
                    } else if (colorMode.getMode() == ColourModes.KV) {
                        FontUtil.two.drawString(m.getName(), (float) hudX, (float) y, Utils.Client.customDraw(del));
                        y += mc.fontRendererObj.FONT_HEIGHT - 2;
                        del -= 10;
                    }
                }
            }
        }
    }

    /*
    private void drawLogo(int e) {

        ScaledResolution sr = new ScaledResolution(mc);
        logoHeight = (sr.getScaledHeight() * logoScaleh.getInput()) / 10;
        if (logoLoaded()) {
            double logoWidth = (sr.getScaledWidth() * logoScalew.getInput()) / 8;
            Minecraft.getMinecraft().getTextureManager().bindTexture(ravenLogo);
            GL11.glColor4f(1, 1, 1, 1);
            if ((positionMode == Utils.HUD.PositionMode.DOWNRIGHT) || (positionMode == Utils.HUD.PositionMode.UPRIGHT)) {
				Gui.drawModalRectWithCustomSizedTexture((int) ((hudX + e) - logoWidth), hudY, 0, 0, (int) logoWidth,
						(int) logoHeight, (int) logoWidth, (int) logoHeight);
            } else {
                Gui.drawModalRectWithCustomSizedTexture(hudX, hudY, 0, 0, (int) logoWidth, (int) logoHeight,
                        (int) logoWidth, (int) logoHeight);
            }
        } else
			logoHeight = 0;
    }
     */

    static class EditHudPositionScreen extends GuiScreen {
        final String hudTextExample = "This is an-Example-HUD";
        GuiButton resetPosButton;
        boolean mouseDown;
        int textBoxStartX;
        int textBoxStartY;
        ScaledResolution sr;
        int textBoxEndX;
        int textBoxEndY;
        int marginX = 5;
        int marginY = 70;
        int lastMousePosX;
        int lastMousePosY;
        int sessionMousePosX;
        int sessionMousePosY;

        @Override
		public void initGui() {
            super.initGui();
            this.buttonList
                    .add(this.resetPosButton = new GuiButton(1, this.width - 90, 5, 85, 20, "Reset position"));
            this.marginX = hudX;
            this.marginY = hudY;
            sr = new ScaledResolution(mc);
            positionMode = Utils.HUD.getPostitionMode(marginX, marginY, sr.getScaledWidth(), sr.getScaledHeight());
            e = false;
        }

        @Override
		public void drawScreen(int mX, int mY, float pt) {
            drawRect(0, 0, this.width, this.height, -1308622848);
            drawRect(0, this.height / 2, this.width, (this.height / 2) + 1, 0x9936393f);
            drawRect(this.width / 2, 0, (this.width / 2) + 1, this.height, 0x9936393f);
            int textBoxStartX = this.marginX;
            int textBoxStartY = this.marginY;
            int textBoxEndX = textBoxStartX + 50;
            int textBoxEndY = textBoxStartY + 32;
            this.drawArrayList(this.mc.fontRendererObj, this.hudTextExample);
            this.textBoxStartX = textBoxStartX;
            this.textBoxStartY = textBoxStartY;
            this.textBoxEndX = textBoxEndX;
            this.textBoxEndY = textBoxEndY;
            hudX = textBoxStartX;
            hudY = textBoxStartY;
            ScaledResolution res = new ScaledResolution(this.mc);
            int descriptionOffsetX = (res.getScaledWidth() / 2) - 84;
            int descriptionOffsetY = (res.getScaledHeight() / 2) - 20;
            Utils.HUD.drawColouredText("Edit the HUD position by dragging.", '-', descriptionOffsetX,
                    descriptionOffsetY, 2L, 0L, true, this.mc.fontRendererObj);
                this.handleInput();

            super.drawScreen(mX, mY, pt);
        }

        private void drawArrayList(FontRenderer fr, String t) {
            int x = this.textBoxStartX;
            int gap = this.textBoxEndX - this.textBoxStartX;
            int y = this.textBoxStartY;
            double marginY = fr.FONT_HEIGHT + 2;
            String[] var4 = t.split("-");
            ArrayList<String> var5 = Utils.Java.toArrayList(var4);
            if ((positionMode == Utils.HUD.PositionMode.UPLEFT) || (positionMode == Utils.HUD.PositionMode.UPRIGHT))
				var5.sort((o1, o2) -> Utils.mc.fontRendererObj.getStringWidth(o2)
                        - Utils.mc.fontRendererObj.getStringWidth(o1));
			else if ((positionMode == Utils.HUD.PositionMode.DOWNLEFT)
                    || (positionMode == Utils.HUD.PositionMode.DOWNRIGHT))
				var5.sort(Comparator.comparingInt(o2 -> Utils.mc.fontRendererObj.getStringWidth(o2)));

            if ((positionMode == Utils.HUD.PositionMode.DOWNRIGHT) || (positionMode == Utils.HUD.PositionMode.UPRIGHT))
				for (String s : var5) {
                    fr.drawString(s, (float) x + (gap - fr.getStringWidth(s)), (float) y, Color.white.getRGB(),
                            dropShadow.isToggled());
                    y += marginY;
                }
			else
				for (String s : var5) {
                    fr.drawString(s, (float) x, (float) y, Color.white.getRGB(), dropShadow.isToggled());
                    y += marginY;
                }
        }

        @Override
		protected void mouseClickMove(int mousePosX, int mousePosY, int clickedMouseButton, long timeSinceLastClick) {
            super.mouseClickMove(mousePosX, mousePosY, clickedMouseButton, timeSinceLastClick);
            if (clickedMouseButton == 0)
				if (this.mouseDown) {
                    this.marginX = this.lastMousePosX + (mousePosX - this.sessionMousePosX);
                    this.marginY = this.lastMousePosY + (mousePosY - this.sessionMousePosY);
                    sr = new ScaledResolution(mc);
                    positionMode = Utils.HUD.getPostitionMode(marginX, marginY, sr.getScaledWidth(),
                            sr.getScaledHeight());

                    // in the else if statement, we check if the mouse is clicked AND inside the
                    // "text box"
                } else if ((mousePosX > this.textBoxStartX) && (mousePosX < this.textBoxEndX)
                        && (mousePosY > this.textBoxStartY) && (mousePosY < this.textBoxEndY)) {
                    this.mouseDown = true;
                    this.sessionMousePosX = mousePosX;
                    this.sessionMousePosY = mousePosY;
                    this.lastMousePosX = this.marginX;
                    this.lastMousePosY = this.marginY;
                }
        }

        @Override
		protected void mouseReleased(int mX, int mY, int state) {
            super.mouseReleased(mX, mY, state);
            if (state == 0)
				this.mouseDown = false;

        }

        @Override
		public void actionPerformed(GuiButton b) {
            if (b == this.resetPosButton) {
                this.marginX = hudX = 5;
                this.marginY = hudY = 70;
            }

        }

        @Override
		public boolean doesGuiPauseGame() {
            return false;
        }
    }

    public enum ColourModes {
        RAVEN, RAVEN2, ASTOLFO, ASTOLFO2, ASTOLFO3, KV
    }

    public static int getHudX() {
        return hudX;
    }

    public static int getHudY() {
        return hudY;
    }

    public static void setHudX(int hudX) {
        HUD.hudX = hudX;
    }

    public static void setHudY(int hudY) {
        HUD.hudY = hudY;
    }
}
