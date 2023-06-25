package me.pianopenguin471.mixins;

import net.weavemc.loader.api.event.EventBus;
import me.pianopenguin471.events.AttackEntityEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerControllerMP.class)
public class AttackMixin {
    @Inject(method = "attackEntity", at = @At("HEAD"))
    public void onAttackEntity(EntityPlayer playerIn, Entity targetEntity, CallbackInfo ci) {
        EventBus.callEvent(new AttackEntityEvent(Minecraft.getMinecraft(), targetEntity, playerIn));
    }
}
