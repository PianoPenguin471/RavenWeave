package keystrokesmod.client.module.modules.movement;

import club.maxstats.weave.loader.api.event.SubscribeEvent;
import club.maxstats.weave.loader.api.event.TickEvent;
import com.google.common.eventbus.Subscribe;
import keystrokesmod.client.event.EventDirection;
import keystrokesmod.client.event.impl.PacketEvent;
import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.setting.impl.DoubleSliderSetting;
import keystrokesmod.client.module.setting.impl.SliderSetting;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class VerusLongJump extends Module {
    public boolean hasJumped = false, shouldJump = false;
    public static SliderSetting speed;

    public VerusLongJump() {
        super("VerusLongJump", ModuleCategory.beta);
        this.registerSetting(speed = new SliderSetting("Speed:", 5, 1, 10, 0.25));
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (event.getDirection() == EventDirection.OUTGOING) return;
        if (!(event.getPacket() instanceof S12PacketEntityVelocity)) return;
        if (((S12PacketEntityVelocity) event.getPacket()).getEntityID() == mc.thePlayer.getEntityId())
            this.shouldJump = true;
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (this.shouldJump) {
            if (!this.hasJumped) {
                this.hasJumped = true;
                if (Module.mc.thePlayer.onGround) {
                    Module.mc.thePlayer.jump();
                }
            } else {
                if (Module.mc.thePlayer.onGround || Module.mc.thePlayer.isCollidedHorizontally) {
                    this.shouldJump = false;
                    this.hasJumped = false;
                    return;
                }

                double s = 1.94D * speed.getInput();
                double r = Math.toRadians(Module.mc.thePlayer.rotationYaw + 90.0F);
                Module.mc.thePlayer.motionX = s * Math.cos(r);
                Module.mc.thePlayer.motionZ = s * Math.sin(r);
            }
        }
    }

    @Override
    public void onDisable() {
        this.shouldJump = false;
        this.hasJumped = false;
        super.onDisable();
    }
}
