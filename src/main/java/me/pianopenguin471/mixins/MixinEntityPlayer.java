package me.pianopenguin471.mixins;

import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatBase;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(priority = 995, value = EntityPlayer.class)
public abstract class MixinEntityPlayer extends EntityLivingBase {

    public MixinEntityPlayer(World p_i1594_1_) {
        super(p_i1594_1_);
    }

    @Shadow
    public abstract ItemStack getHeldItem();

    @Shadow
    public abstract void onCriticalHit(Entity p_onCriticalHit_1_);

    @Shadow
    public abstract void onEnchantmentCritical(Entity p_onEnchantmentCritical_1_);

    @Shadow
    public abstract void triggerAchievement(StatBase p_triggerAchievement_1_);

    @Shadow
    public abstract ItemStack getCurrentEquippedItem();

    @Shadow
    public abstract void destroyCurrentEquippedItem();

    @Shadow
    public abstract void addStat(StatBase p_addStat_1_, int p_addStat_2_);

    @Shadow
    public abstract void addExhaustion(float p_addExhaustion_1_);
}
