package me.pianopenguin471.mixins;

import ravenweave.client.module.modules.render.Xray;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.nio.ByteOrder;
import java.nio.IntBuffer;
import ravenweave.client.Raven;

@Mixin(value = WorldRenderer.class, priority = 999)
public abstract class WorldRendererMixin {

    @Shadow public abstract int getColorIndex(int p_getColorIndex_1_);

    @Shadow private boolean noColor;
    @Shadow private IntBuffer rawIntBuffer;

    /**
     * @author mc code
     * @reason aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
     */
    @Overwrite
    public void putColorMultiplier(float p_putColorMultiplier_1_, float p_putColorMultiplier_2_, float p_putColorMultiplier_3_, int p_putColorMultiplier_4_) {
        int i = this.getColorIndex(p_putColorMultiplier_4_);
        int j = -1;

        if (!this.noColor) {
            j = this.rawIntBuffer.get(i);
            int k;
            int l;
            int i1;
            if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
                k = (int) ((float) (j & 255) * p_putColorMultiplier_1_);
                l = (int) ((float) (j >> 8 & 255) * p_putColorMultiplier_2_);
                i1 = (int) ((float) (j >> 16 & 255) * p_putColorMultiplier_3_);
                j &= -16777216;
                j = j | i1 << 16 | l << 8 | k;
            } else {
                k = (int) ((float) (j >> 24 & 255) * p_putColorMultiplier_1_);
                l = (int) ((float) (j >> 16 & 255) * p_putColorMultiplier_2_);
                i1 = (int) ((float) (j >> 8 & 255) * p_putColorMultiplier_3_);
                j &= 255;
                j = j | k << 24 | l << 16 | i1 << 8;
            }

            if(Raven.moduleManager.getModuleByClazz(Xray.class).isEnabled()) {
                j = getColor(k, l, i1, (int) Xray.opacity.getInput());
            }
        }

        this.rawIntBuffer.put(i, j);
    }

    public int getColor(int red, int green, int blue, int alpha) {
        int color = MathHelper.clamp_int(alpha, 0, 255) << 24;
        color |= MathHelper.clamp_int(red, 0, 255) << 16;
        color |= MathHelper.clamp_int(green, 0, 255) << 8;
        color |= MathHelper.clamp_int(blue, 0, 255);
        return color;
    }

}