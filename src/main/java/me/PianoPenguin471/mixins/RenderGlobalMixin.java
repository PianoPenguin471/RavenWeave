package me.PianoPenguin471.mixins;

import net.weavemc.loader.api.event.EventBus;
import me.PianoPenguin471.events.DrawBlockHighlightEvent;
import net.minecraft.client.renderer.RenderGlobal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderGlobal.class)
public abstract class RenderGlobalMixin {
    @Inject(method = "drawSelectionBox", at = @At("HEAD"))
    public void onDrawSelectionBox(CallbackInfo ci) {
        System.out.println("Drawing Selection Box");
        EventBus.callEvent(new DrawBlockHighlightEvent());
    }
}
