package me.pianopenguin471.mixins;

import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import ravenweave.client.Raven;
import ravenweave.client.module.modules.render.AntiShuffle;

@Mixin(FontRenderer.class)
public abstract class FontRendererMixin {
    @ModifyVariable(method = "renderString", at = @At(value = "HEAD"), argsOnly = true)
    private String renderStringHook(String s) {
        if (Raven.moduleManager.getModuleByClazz(AntiShuffle.class).isEnabled()) {
         return AntiShuffle.getUnformattedTextForChat(s);
      } else {
         return s;
      }
    }
}

