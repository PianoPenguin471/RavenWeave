package me.pianopenguin471.mixins;

import net.minecraft.client.entity.EntityPlayerSP;
import net.weavemc.loader.api.event.EventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ravenweave.client.event.impl.LivingUpdateEvent;

@Mixin(EntityPlayerSP.class)
public class EntityPlayerSPMixin {
    @Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;setSprinting(Z)V", ordinal = 2))
    public void onLivingUpdate(EntityPlayerSP entityPlayerSP, boolean sprinting) {
        LivingUpdateEvent event = new LivingUpdateEvent(entityPlayerSP, sprinting);
        EventBus.callEvent(event);

        if (event.isCancelled())
            event.getEntity().setSprinting(true);
        else
            entityPlayerSP.setSprinting(sprinting);
    }
}
