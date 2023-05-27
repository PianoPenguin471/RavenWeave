package me.PianoPenguin471.mixins;

import keystrokesmod.client.module.modules.combat.KillAura;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static keystrokesmod.client.main.Raven.mc;

@Mixin(RendererLivingEntity.class)
public abstract class RendererLivingEntityMixin<T extends EntityLivingBase> extends Render<T> {
    protected RendererLivingEntityMixin(RenderManager renderManager) {
        super(renderManager);
    }

    @ModifyVariable(method = "doRender(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V", at = @At("HEAD"), ordinal = 4)
    public float changeYaw(float input) {
        System.out.println(input);
        return input + 90;
    }

    @Inject(method = "doRender(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V", at=@At("HEAD"))
    public void onDoRender(T entityIn, double var2, double var4, double var6, float yaw, float partialTicks, CallbackInfo ci) {
        if (!(entityIn instanceof EntityPlayerSP)) return;
        /*System.out.println(
                "var2: " + var2 +
                ", var4: " + var4 +
                ", var6: " + var6 +
                ", yaw: " + yaw +
                ", partialTicks: " + partialTicks
        );*/
    }
}
