package me.pianopenguin471.mixins;

import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.entity.Entity;
import ravenweave.client.event.impl.HitSlowDownEvent;
import ravenweave.client.main.Raven;

@Mixin(EntityPlayer.class)
public abstract class MixinAttackEntity {

    public double newSlowDown;
    public boolean newSprinting;

    @Inject(method = "attackTargetEntityWithCurrentItem", at = @At("INVOKE"), remap = false)
    private void onAttackTarget(Entity targetEntity, CallbackInfo info) {
        double slowDown = 0.6D;
        boolean isSprinting = false;

        HitSlowDownEvent hitSlowDown = new HitSlowDownEvent(slowDown, isSprinting);
        Raven.eventBus.post(hitSlowDown);

        newSlowDown = hitSlowDown.getSlowDown();
        newSprinting = hitSlowDown.isSprinting();
    }

    @ModifyVariable(method = "attackTargetEntityWithCurrentItem", at = @At(value = "STORE", ordinal = 0), remap = false)
    private double modifyMotionX(double originalMotionX) {
        return newSlowDown;
    }

    @ModifyVariable(method = "attackTargetEntityWithCurrentItem", at = @At(value = "STORE", ordinal = 1), remap = false)
    private double modifyMotionZ(double originalMotionZ) {
        return newSlowDown;
    }

    @ModifyVariable(method = "attackTargetEntityWithCurrentItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setSprinting(Z)V"), remap = false)
    private boolean modifySprinting(boolean originalSprinting) {
        return newSprinting;
    }
}
