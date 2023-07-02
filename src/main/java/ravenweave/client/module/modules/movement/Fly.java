package ravenweave.client.module.modules.movement;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.Minecraft;
import ravenweave.client.event.impl.TickEvent;
import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.ComboSetting;
import ravenweave.client.module.setting.impl.DescriptionSetting;
import ravenweave.client.module.setting.impl.SliderSetting;
import ravenweave.client.utils.Utils;

public class Fly extends Module {
    private final VanFly vanFly = new VanFly();
    private final GliFly gliFly = new GliFly();
    public static DescriptionSetting description;
    public static SliderSetting speed;
    public static ComboSetting<FlyMode> mode;

    public Fly() {
        super("Fly", ModuleCategory.movement);
        this.registerSetting(description = new DescriptionSetting("Lets you fly"));
        this.registerSetting(mode = new ComboSetting<>(Utils.md, FlyMode.VANILLA));
        this.registerSetting(speed = new SliderSetting("Speed", 2.0D, 1.0D, 5.0D, 0.1D));
    }

    public void onEnable() {
        switch (mode.getMode()) {
            case VANILLA:
                vanFly.onEnable();
                break;
            case GLIDE:
                gliFly.onEnable();
                break;
        }
    }

    public void onDisable() {
        switch (mode.getMode()) {
            case VANILLA:
                vanFly.onDisable();
                break;
            case GLIDE:
                gliFly.onDisable();
                break;
        }
    }

    @Subscribe
    public void onTick(TickEvent e) {
        switch (mode.getMode()) {
            case VANILLA:
                vanFly.update();
                break;
            case GLIDE:
                gliFly.update();
                break;
        }
    }

    private class GliFly {
        private boolean opf;

        public void onEnable() {
        }

        public void onDisable() {
            opf = false;
        }

        public void update() {
            if (Module.mc.thePlayer.movementInput.moveForward > 0.0F) {
                if (!opf) {
                    opf = true;
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

    private static class VanFly {
        private static final float DEFAULT_SPEED = 0.05F;

        public void onEnable() {
        }

        public void onDisable() {
            if (Minecraft.getMinecraft().thePlayer == null)
                return;

            if (Minecraft.getMinecraft().thePlayer.capabilities.isFlying) {
                Minecraft.getMinecraft().thePlayer.capabilities.isFlying = false;
            }

            Minecraft.getMinecraft().thePlayer.capabilities.setFlySpeed(DEFAULT_SPEED);
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