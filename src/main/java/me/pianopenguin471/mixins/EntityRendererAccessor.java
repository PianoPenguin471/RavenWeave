package me.pianopenguin471.mixins;

import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityRenderer.class)
public interface EntityRendererAccessor {
    @Accessor("lightmapUpdateNeeded")
    boolean getLightmapUpdateNeeded();

    @Accessor("lightmapUpdateNeeded")
    void setLightmapUpdateNeeded(boolean lightmapUpdateNeeded);

    @Accessor("lightmapColors")
    int[] getLightmapColors();

    @Accessor("lightmapTexture")
    DynamicTexture getLightmapTexture();

    @Accessor("bossColorModifier")
    float getBossColorModifier();

    @Accessor("bossColorModifierPrev")
    float getBossColorModifierPrev();

    @Accessor("torchFlickerX")
    float getTorchFlickerX();
}
