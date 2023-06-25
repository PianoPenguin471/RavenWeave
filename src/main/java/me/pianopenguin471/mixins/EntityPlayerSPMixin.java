package me.pianopenguin471.mixins;

import net.weavemc.loader.api.event.EventBus;
import me.pianopenguin471.events.LivingUpdateEvent;
import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityPlayerSP.class)
public class EntityPlayerSPMixin {
    @Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;setSprinting(Z)V", ordinal = 2))
    public void onLivingUpdate(EntityPlayerSP entityPlayerSP, boolean sprinting) {
        LivingUpdateEvent livingUpdateEvent = new LivingUpdateEvent(entityPlayerSP, sprinting);
        EventBus.callEvent(livingUpdateEvent);

        if (livingUpdateEvent.isCancelled())
            livingUpdateEvent.getEntity().setSprinting(true);
        else
            entityPlayerSP.setSprinting(sprinting);
    }
}
