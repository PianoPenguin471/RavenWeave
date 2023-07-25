package me.pianopenguin471.mixins;

import ravenweave.client.event.impl.LivingUpdateEvent;
import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ravenweave.client.main.Raven;

@Mixin(EntityPlayerSP.class)
public class EntityPlayerSPMixin {
    @Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;setSprinting(Z)V", ordinal = 2))
    public void onLivingUpdate(EntityPlayerSP entityPlayerSP, boolean sprinting) {
        LivingUpdateEvent event = new LivingUpdateEvent(entityPlayerSP, sprinting);
        Raven.eventBus.post(event);

        if (event.isCancelled())
            event.getEntity().setSprinting(true);
        else
            entityPlayerSP.setSprinting(sprinting);
    }
}
