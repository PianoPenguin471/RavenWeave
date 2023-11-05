package me.pianopenguin471.mixins;

import ravenweave.client.module.modules.render.Xray;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.util.EnumWorldBlockLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ravenweave.client.Raven;

@Mixin(priority = 1005, value = BlockGrass.class)
public class BlockGrassMixin extends Block {

    public BlockGrassMixin(Material p_i46399_1_, MapColor p_i46399_2_) {
        super(p_i46399_1_, p_i46399_2_);
    }

    @Inject(method = "getBlockLayer", at = @At("HEAD"), cancellable = true)
    public void getBlockLayer(CallbackInfoReturnable<EnumWorldBlockLayer> cir) {
        if(Raven.moduleManager.getModuleByClazz(Xray.class).isEnabled()) {
            cir.setReturnValue(super.getBlockLayer());
        }
    }

}