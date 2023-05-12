package keystrokesmod.client.module.modules.movement;

import com.google.common.eventbus.Subscribe;
import keystrokesmod.client.event.impl.TickEvent;
import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.setting.impl.ComboSetting;
import keystrokesmod.client.module.setting.impl.SliderSetting;
import keystrokesmod.client.utils.Utils;
import net.minecraft.client.Minecraft;

public class Fly extends Module {
    private final Fly.VanFly vanFly = new VanFly();
    private final Fly.GliFly gliFly = new Fly.GliFly();
    public static SliderSetting speed;
    public static ComboSetting<FlyMode> mode;

    public Fly() {
        super("Fly", ModuleCategory.movement);
        this.registerSetting(mode = new ComboSetting<>(Utils.md, FlyMode.VANILLA));
        this.registerSetting(speed = new SliderSetting("Speed", 2.0D, 1.0D, 5.0D, 0.1D));
    }

    public void onEnable() {
        switch (mode.getMode()) {
            case VANILLA:
                this.vanFly.onEnable();
                break;
            case GLIDE:
                this.gliFly.onEnable();
        }

    }

    public void onDisable() {
        switch (mode.getMode()) {
            case VANILLA:
                this.vanFly.onDisable();
                break;
            case GLIDE:
                this.gliFly.onDisable();
        }

    }

    @Subscribe
    public void onTick(TickEvent e) {
        switch (mode.getMode()) {
            case VANILLA:
                this.vanFly.update();
                break;
            case GLIDE:
                this.gliFly.update();
        }

    }

    class GliFly {
        boolean opf;

        public void onEnable() {
        }

        public void onDisable() {
            this.opf = false;
        }

        public void update() {
            if (Module.mc.thePlayer.movementInput.moveForward > 0.0F) {
                if (!this.opf) {
                    this.opf = true;
                    if (Module.mc.thePlayer.onGround) {
                        Module.mc.thePlayer.jump();
                    }
                } else {
                    if (Module.mc.thePlayer.onGround || Module.mc.thePlayer.isCollidedHorizontally) {
                        Fly.this.disable();
                        return;
                    }

                    double s = 1.94D * speed.getInput();
                    double r = Math.toRadians(Module.mc.thePlayer.rotationYaw + 90.0F);
                    Module.mc.thePlayer.motionX = s * Math.cos(r);
                    Module.mc.thePlayer.motionZ = s * Math.sin(r);
                }
            }

        }
    }

    static class VanFly {
        private final float dfs = 0.05F;

        public void onEnable() {
        }

        public void onDisable() {
            if (Minecraft.getMinecraft().thePlayer == null)
                return;

            if (Minecraft.getMinecraft().thePlayer.capabilities.isFlying) {
                Minecraft.getMinecraft().thePlayer.capabilities.isFlying = false;
            }

            Minecraft.getMinecraft().thePlayer.capabilities.setFlySpeed(0.05F);
        }

        public void update() {
            Module.mc.thePlayer.motionY = 0.0D;
            Module.mc.thePlayer.capabilities.setFlySpeed((float) (0.05000000074505806D * speed.getInput()));
            Module.mc.thePlayer.capabilities.isFlying = true;
        }
    }

    public enum FlyMode {
        VANILLA,
        GLIDE
    }
}
