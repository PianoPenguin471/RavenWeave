package me.pianopenguin471.mixins;

import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.weavemc.loader.api.event.EventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ravenweave.client.event.impl.RenderLabelEvent;

@Mixin(RendererLivingEntity.class)
public abstract class RenderNameMixin {
    @Inject(method = "renderName(Lnet/minecraft/entity/EntityLivingBase;DDD)V", at = @At("HEAD"), cancellable = true)
    private void onRenderLabel(EntityLivingBase entity, double x, double y, double z, CallbackInfo ci) {
        RenderLabelEvent e = new RenderLabelEvent(entity, x, y, z);
        EventBus.callEvent(e);
        if (e.isCancelled())
            ci.cancel();
    }
}