package me.pianopenguin471.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.weavemc.loader.api.event.EventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import ravenweave.client.event.impl.LookEvent;

@Mixin(priority = 1005, value = Entity.class)
public abstract class MixinEntity2 {

    @Shadow
    public float rotationYaw;

    @Shadow
    public float rotationPitch;

   /**
    * @author mc code
    * @reason look event
    */
   @Overwrite
   public final Vec3 getVectorForRotation(float pitch, float yaw) {
       if((Object) this == Minecraft.getMinecraft().thePlayer) {
           LookEvent e = new LookEvent(pitch, yaw);
           EventBus.callEvent(e);
           pitch = e.getPitch();
           yaw = e.getYaw();
       }
       float f = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
       float f1 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
       float f2 = -MathHelper.cos(-pitch * 0.017453292F);
       float f3 = MathHelper.sin(-pitch * 0.017453292F);
       return new Vec3(f1 * f2, f3, f * f2);
   }

}
