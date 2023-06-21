package keystrokesmod.client.module.modules.client;

import keystrokesmod.client.clickgui.raven.components.CategoryComponent;
import keystrokesmod.client.clickgui.theme.Theme;
import keystrokesmod.client.clickgui.theme.themes.*;
import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.setting.Setting;
import keystrokesmod.client.module.setting.impl.ComboSetting;
import keystrokesmod.client.module.setting.impl.TickSetting;
import keystrokesmod.client.utils.ColorM;
import keystrokesmod.client.utils.Utils;

public class ClickGuiModule extends Module {

    private static ComboSetting preset;

    private static TickSetting cleanUp, reset, betagui;

    public static int guiScale;

    public ClickGuiModule() {
        super("Gui", ModuleCategory.client);
        withKeycode(54);

        this.registerSetting(betagui = new TickSetting("beta gui (VERY BETA)", false));
        this.registerSetting(cleanUp = new TickSetting("Clean Up", false));
        this.registerSetting(reset = new TickSetting("Reset position", false));
        this.registerSetting(preset = new ComboSetting("Preset", Preset.PlusPlus));
    }

    @Override
    public void guiButtonToggled(Setting setting) {
        if (setting == cleanUp) {
            cleanUp.disable();
            for (CategoryComponent cc : Raven.clickGui.getCategoryList())
                cc.setCoords(((cc.getX() / 50) * 50) + ((cc.getX() % 50) > 25 ? 50 : 0), ((cc.getY() / 50) * 50) + ((cc.getY() % 50) > 25 ? 50 : 0));
        } else if (setting == reset) {
            reset.disable();
            Raven.clickGui.resetSort();
        }

    }

    @Override
    public void onEnable() {
        if (Utils.Player.isPlayerInGame() && ((mc.currentScreen != Raven.clickGui) || (mc.currentScreen != Raven.kvCompactGui)))
            if(betagui.isToggled()) {
                guiScale = mc.gameSettings.guiScale;
                mc.gameSettings.guiScale = 3;
                mc.displayGuiScreen(Raven.kvCompactGui);
                Raven.kvCompactGui.initGui();
                Raven.kvCompactGui.initGui();
            }
            else {
                mc.displayGuiScreen(Raven.clickGui);
                Raven.clickGui.initMain();
            }

        this.disable();
    }


    private static Preset getPresetMode() {
        return (Preset) preset.getMode();
    }

    public static boolean isCategoryBackgroundToggled() {
        return getPresetMode().categoryBackground;
    }

    public static boolean showGradientEnabled() {
        return getPresetMode().showGradientEnabled;
    }

    public static boolean showGradientDisabled() {
        return  getPresetMode().showGradientDisabled;
    }

    public static boolean useCustomFont() {
        return  getPresetMode().useCustomFont;
    }

    public static int getEnabledTopRGB(int delay) {
        return  getPresetMode().enabledTopRGB.color(delay);
    }

    public static int getEnabledTopRGB() {
        return getEnabledTopRGB(0);
    }

    public static int getEnabledBottomRGB(int delay) {
        return  getPresetMode().enabledBottomRGB.color(delay);
    }

    public static int getEnabledBottomRGB() {
        return getEnabledBottomRGB(0);
    }

    public static int getEnabledTextRGB(int delay) {
        return  getPresetMode().enabledTextRGB.color(delay);
    }

    public static int getEnabledTextRGB() {
        return getEnabledTextRGB(0);
    }

    public static int getDisabledTopRGB(int delay) {
        return  getPresetMode().disabledTopRGB.color(delay);
    }

    public static int getDisabledTopRGB() {
        return getDisabledTopRGB(0);
    }

    public static int getDisabledBottomRGB(int delay) {
        return  getPresetMode().disabledBottomRGB.color(delay);
    }

    public static int getDisabledBottomRGB() {
        return getDisabledBottomRGB(0);
    }

    public static int getDisabledTextRGB(int delay) {
        return  getPresetMode().disabledTextRGB.color(0);
    }

    public static int getDisabledTextRGB() {
        return getDisabledTextRGB(0);
    }

    public static int getBackgroundRGB(int delay) {
        return  getPresetMode().backgroundRGB.color(delay);
    }

    public static int getBackgroundRGB() {
        return getBackgroundRGB(0);
    }

    public static int getSettingBackgroundRGB(int delay) {
        return  getPresetMode().settingBackgroundRGB.color(delay);
    }

    public static int getSettingBackgroundRGB() {
        return getSettingBackgroundRGB(0);
    }


    public static int getCategoryBackgroundRGB(int delay) {
        return  getPresetMode().categoryBackgroundRGB.color(delay);
    }

    public static int getCategoryBackgroundRGB() {
        return getCategoryBackgroundRGB(0);
    }

    public static int getCategoryNameRGB(int delay) {
        return  getPresetMode().categoryNameRGB.color(delay);
    }

    public static int getCategoryNameRGB() {
        return getCategoryNameRGB(0);
    }

    public static int getBoarderColour(int delay) {
        return  getPresetMode().boarderColor.color(delay);
    }

    public static int getBoarderColour() {
        return getBoarderColour(0);
    }

    public static int getCategoryOutlineColor1(int delay) {
        return  getPresetMode().categoryOutlineColor.color(delay);
    }

    public static int getCategoryOutlineColor1() {
        return getCategoryOutlineColor1(0);
    }

    public static int getCategoryOutlineColor2(int delay) {
        return  getPresetMode().categoryOutlineColor2.color(delay);
    }

    public static int getCategoryOutlineColor2() {
        return getCategoryOutlineColor2(0);
    }

    public static CNColor getCNColor() {
        return  getPresetMode().cnColor;
    }

    public static boolean isSwingToggled() {
        return  getPresetMode().swing;
    }

    public static boolean isRoundedToggled() {
        return  getPresetMode().swing;
    }

    public static boolean isBoarderToggled() {
        return  getPresetMode().boarder;
    }

    public enum Preset {
        Vape(new Vape()),
        ArcDark(new ArcDark()),
        MaterialDark(new MaterialDark()),

        PlusPlus(true, false, true, true, CNColor.STATIC, in -> 0xFFFFFFFE, in -> -15001318, in -> -15001318, in -> Utils.Client.rainbowDraw(2, in), in -> Utils.Client.rainbowDraw(2, in), in -> 0xFF000000, in -> 0xFF000000,in -> 0xFF000000, in -> 0xFFFFFFFE, in -> 0xFF808080, true, true, true, in -> 0xFFFFFFFE, in -> Utils.Client.astolfoColorsDraw(in, 10), in -> Utils.Client.otherAstolfoColorsDraw(in, 10));

        public boolean showGradientEnabled, showGradientDisabled, useCustomFont, categoryBackground, roundedCorners, swing, boarder;
        public ColorM categoryNameRGB, settingBackgroundRGB, categoryBackgroundRGB, enabledTopRGB, enabledBottomRGB,
        enabledTextRGB, disabledTopRGB, disabledBottomRGB, disabledTextRGB, backgroundRGB, boarderColor, categoryOutlineColor, categoryOutlineColor2;
        public CNColor cnColor;

        private Preset(
                        boolean showGradientEnabled, boolean showGradientDisabled, boolean useCustomFont,
                        boolean categoryBackground, CNColor cnColor, ColorM categoryNameRGB, ColorM settingBackgroundRGB,
                        ColorM categoryBackgroundRGB, ColorM enabledTopRGB, ColorM enabledBottomRGB, ColorM enabledTextRGB,
                        ColorM disabledTopRGB, ColorM disabledBottomRGB, ColorM disabledTextRGB, ColorM backgroundRGB,
                        boolean roundedCorners, boolean swing, boolean boarder, ColorM boarderColor, ColorM categoryOutlineColor, ColorM categoryOutlineColor2) {
            this.showGradientEnabled = showGradientEnabled;
            this.showGradientDisabled = showGradientDisabled;
            this.useCustomFont = useCustomFont;
            this.categoryBackground = categoryBackground;
            this.categoryNameRGB = categoryNameRGB;
            this.settingBackgroundRGB = settingBackgroundRGB;
            this.categoryBackgroundRGB = categoryBackgroundRGB;
            this.enabledTopRGB = enabledTopRGB;
            this.enabledBottomRGB = enabledBottomRGB;
            this.enabledTextRGB = enabledTextRGB;
            this.disabledTopRGB = disabledTopRGB;
            this.disabledBottomRGB = disabledBottomRGB;
            this.disabledTextRGB = disabledTextRGB;
            this.backgroundRGB = backgroundRGB;
            this.cnColor = cnColor;
            this.roundedCorners = roundedCorners;
            this.swing = swing;
            this.boarder = boarder;
            this.boarderColor = boarderColor;
            this.categoryOutlineColor = categoryOutlineColor;
            this.categoryOutlineColor2 = categoryOutlineColor2;
        }

        private Preset(Theme theme) {
            this.showGradientEnabled = true;
            this.showGradientDisabled = false;
            this.useCustomFont = true;
            this.categoryBackground = true;
            this.categoryNameRGB = in -> theme.getTextColour().getRGB();
            this.settingBackgroundRGB = in -> theme.getAccentColour().getRGB();
            this.categoryBackgroundRGB = in -> theme.getBackgroundColour().getRGB();
            this.enabledTopRGB = in -> theme.getBackgroundColour().getRGB();
            this.enabledBottomRGB = in -> theme.getSecondBackgroundColour().getRGB();
            this.enabledTextRGB = in -> theme.getTextColour().getRGB();
            this.disabledTopRGB = in -> theme.getDisabledColour().getRGB();
            this.disabledBottomRGB = in -> theme.getDisabledColour().getRGB();
            this.disabledTextRGB = in -> theme.getDisabledColour().getRGB();
            this.backgroundRGB = in -> theme.getDisabledColour().getRGB();
            this.cnColor = CNColor.STATIC;
            this.roundedCorners = true;
            this.swing = true;
            this.boarder = true;
            this.boarderColor = in -> theme.getBorderColour().getRGB();
            this.categoryOutlineColor = in -> theme.getBorderColour().getRGB();
            this.categoryOutlineColor2 = in -> theme.getSelectionForegroundColour().getRGB();
        }

    }

    public enum CNColor {
        RAINBOW, STATIC
    }
}
