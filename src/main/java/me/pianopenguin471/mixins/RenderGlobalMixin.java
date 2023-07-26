package me.pianopenguin471.mixins;

import ravenweave.client.event.impl.DrawBlockHighlightEvent;
import net.minecraft.client.renderer.RenderGlobal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ravenweave.client.main.Raven;

@Mixin(RenderGlobal.class)
public abstract class RenderGlobalMixin {
    @Inject(method = "drawSelectionBox", at = @At("HEAD"))
    public void onDrawSelectionBox(CallbackInfo ci) {
        Raven.eventBus.post(new DrawBlockHighlightEvent());
    }
}
