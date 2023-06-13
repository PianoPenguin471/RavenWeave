package me.PianoPenguin471.mixins;

import net.weavemc.loader.api.event.EventBus;
import me.PianoPenguin471.events.ClickBlockEvent;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerControllerMP.class)
public class PlayerControllerMPMixin {
    @Inject(method = "clickBlock", at = @At("HEAD"))
    public void onClickBlock(BlockPos loc, EnumFacing face, CallbackInfoReturnable<Boolean> cir) {
        EventBus.callEvent(new ClickBlockEvent(loc));
    }
}
