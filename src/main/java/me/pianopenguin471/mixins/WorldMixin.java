package me.pianopenguin471.mixins;

import ravenweave.client.event.impl.EntityJoinWorldEvent;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ravenweave.client.main.Raven;

@Mixin(World.class)
public class WorldMixin {
    @Inject(method = "spawnEntityInWorld", at = @At("HEAD"))
    public void onSpawnEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        Raven.eventBus.post(new EntityJoinWorldEvent(entity, entity.worldObj));
    }
}
