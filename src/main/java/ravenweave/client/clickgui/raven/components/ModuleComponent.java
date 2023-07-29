package ravenweave.client.clickgui.raven.components;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import ravenweave.client.clickgui.raven.Component;
import ravenweave.client.main.Raven;
import ravenweave.client.module.Module;
import ravenweave.client.module.modules.client.ClickGuiModule;
import ravenweave.client.module.setting.Setting;
import ravenweave.client.utils.RenderUtils;
import ravenweave.client.utils.font.FontUtil;

import java.awt.*;
import java.util.ArrayList;

public class ModuleComponent extends Component {
    public Module mod;
    public CategoryComponent category;
    public ArrayList<SettingComponent> settings = new ArrayList<>();
    public BindComponent bind;
    public int aHeight = 20;

    public ModuleComponent(Module mod, CategoryComponent p) {
        this.mod = mod;
        this.category = p;
        mod.setModuleComponent(this);
        mod.getSettings().forEach(setting -> {
            Class<? extends SettingComponent> clazz = setting.getRavenComponentType();
            try {
                settings.add(clazz.getDeclaredConstructor(Setting.class, this.getClass()).newInstance(setting, this));
            } catch (Exception e) {
                System.out.println(clazz);
            };
        });
        bind = new BindComponent(this);
        setDimensions(p.getWidth(), aHeight);
    }


    public static void e() {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
    }

    public static void f() {
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
        GL11.glEdgeFlag(true);
    }

    public static void g(int rgb) {
        Color c = new Color(rgb);
        float a = 255;
        float r = c.getRed() / 255f;
        float g = c.getGreen() / 255f;
        float b = c.getBlue() / 255f;
        GL11.glColor4f(r, g, b, a);
    }

    public static void v(float x, float y, float x1, float y1, int t, int b) {
        e();
        GL11.glShadeModel(7425);
        GL11.glBegin(7);
        g(t);
        GL11.glVertex2f(x, y1);
        GL11.glVertex2f(x1, y1);
        g(b);
        GL11.glVertex2f(x1, y);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
        GL11.glShadeModel(7424);
        f();
    }

    public static void vr(float x, float y, float x1, float y1, int t, int b) {
        RenderUtils.drawRoundedRect(x, y, x1, y1, 12, t, new boolean[] {false,true,true,false} );
    }

    @Override
    public void draw(int mouseX, int mouseY) {

        //background
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        if (ClickGuiModule.showGradientEnabled() && mod.isEnabled()) v(x, y, x2, y + aHeight, ClickGuiModule.getEnabledBottomRGB((((y + aHeight))) * 20), ClickGuiModule.getEnabledTopRGB((y) * 20));
        if (ClickGuiModule.showGradientDisabled() && !mod.isEnabled()) v(x, y, x2, y + aHeight, ClickGuiModule.getDisabledBottomRGB(((y + aHeight)) * 20), ClickGuiModule.getDisabledTopRGB((y) * 20));
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();

        //name
        int button_rgb = mod.isEnabled() ? ClickGuiModule.getEnabledTextRGB() : this.mod.canBeEnabled() ? ClickGuiModule.getDisabledTextRGB() : 0xFF999999;
        GL11.glPushMatrix();
        if (ClickGuiModule.useCustomFont()) FontUtil.normal.drawCenteredSmoothString(mod.getName(), x + (width/2), y + (aHeight/2), button_rgb);
        else Raven.mc.fontRendererObj.drawString(mod.getName(), (x + (width/2)) - (Raven.mc.fontRendererObj.getStringWidth(mod.getName())/2), y + (aHeight/2), button_rgb, true);
        GL11.glPopMatrix();

        //settings
        if(category.getOpenModule() == this) {
            int yOffset = 0;
            for(SettingComponent setting : settings) {
                if(!setting.visable) continue;
                setting.setCoords(x, y + aHeight + yOffset);
                setting.draw(mouseX, mouseY);
                yOffset += setting.getHeight() + 3;
            }
            if(mod.isBindable()) {
                bind.setCoords(x, y + aHeight + yOffset);
                bind.draw(mouseX, mouseY);
                yOffset += bind.getHeight();
            }
            setDimensions(width, aHeight + yOffset + 3);
        }
    }

    @Override
    public void clicked(int x, int y, int b) {
        if(insideNameArea(x, y))
            if(b == 0) {
                mod.toggle();
                return;
            }
            else if ((b == 1) && !mod.getSettings().isEmpty()) {
                category.setOpenModule(category.getOpenModule() == this ? null : this);
                int yOffset = 0;
                if(category.getOpenModule() == this) {
                    for(SettingComponent setting : settings) {
                        setting.setCoords(x, y + aHeight + yOffset);
                        yOffset += setting.getHeight() + 2;
                    }
                    if(mod.isBindable()) {
                        bind.setCoords(x, y + aHeight + yOffset);
                        yOffset += bind.getHeight();
                    }
                    setDimensions(width, aHeight + yOffset + 3);
                } else
                    setDimensions(category.getWidth(), aHeight);
                return;
            }

        if(category.getOpenModule() == this) settings.forEach(setting -> {if(setting.visable);setting.mouseDown(x, y, b);});
        bind.mouseDown(x, y, b);
    }

    @Override
    public void mouseReleased(int x, int y, int b) {
        if(category.getOpenModule() == this) settings.forEach(setting -> setting.mouseReleased(x, y, b));
    }

    @Override
    public void keyTyped(char t, int k) {
        bind.keyTyped(t,k);
    }


    public boolean insideNameArea(int mouseX, int mouseY) {
        return ((mouseX > (x)) && (mouseX < (x2)) && (mouseY > y) && (mouseY < (y + aHeight)));
    }
}