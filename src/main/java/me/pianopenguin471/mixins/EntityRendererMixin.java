package me.pianopenguin471.mixins;

import com.google.common.base.Predicates;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.world.World;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import ravenweave.client.Raven;
import ravenweave.client.module.Module;
import ravenweave.client.module.modules.combat.HitBoxes;
import ravenweave.client.module.modules.combat.KillAura;
import ravenweave.client.module.modules.combat.Reach;
import ravenweave.client.module.modules.render.Xray;

import java.util.List;

@Mixin(priority = 995, value = EntityRenderer.class)
public abstract class EntityRendererMixin {

    @Shadow
    private Minecraft mc;
    @Shadow
    private Entity pointedEntity;

    @Shadow private boolean lightmapUpdateNeeded;
    @Shadow private float torchFlickerX;
    @Shadow private float bossColorModifier;
    @Shadow private float bossColorModifierPrev;

    @Shadow protected abstract float getNightVisionBrightness(EntityLivingBase p_getNightVisionBrightness_1_, float p_getNightVisionBrightness_2_);

    @Shadow @Final private int[] lightmapColors;
    @Shadow @Final private DynamicTexture lightmapTexture;

    /**
     * @author mc code
     * @reason reach
     */
    @Overwrite
    public void getMouseOver(float p_getMouseOver_1_) {
        Entity entity = this.mc.getRenderViewEntity();
        if ((entity != null) && (this.mc.theWorld != null)) {
            this.mc.mcProfiler.startSection("pick");
            this.mc.pointedEntity = null;
            double reach = this.mc.playerController.getBlockReachDistance();

            this.mc.objectMouseOver = entity.rayTrace(reach, p_getMouseOver_1_);
            double distanceToVec = reach;

            Vec3 vec3 = entity.getPositionEyes(p_getMouseOver_1_);
            boolean flag = false;


            Module reachMod = Raven.moduleManager.getModuleByClazz(Reach.class);
            Module aura = Raven.moduleManager.getModuleByClazz(KillAura.class);

            if (!reachMod.isEnabled() && !aura.isEnabled()) {
                if (this.mc.playerController.extendedReach()) {
                    reach = 6.0D;
                    distanceToVec = 6.0D;
                } else if (reach > 3.0D)
                    flag = true;
            } else if (this.mc.playerController.extendedReach()) {
                reach = 6.0D;
                distanceToVec = 6.0D;
            } else
                reach = Reach.getReach();

            if (this.mc.objectMouseOver != null)
                distanceToVec = this.mc.objectMouseOver.hitVec.distanceTo(vec3);

            Vec3 vec31 = entity.getLook(p_getMouseOver_1_);
            Vec3 vec32 = vec3.addVector(vec31.xCoord * reach, vec31.yCoord * reach, vec31.zCoord * reach);
            this.pointedEntity = null;
            Vec3 vec33 = null;
            float f = 1.0F;
            List<Entity> list = this.mc.theWorld.getEntitiesInAABBexcluding(entity,
                    entity.getEntityBoundingBox()
                            .addCoord(vec31.xCoord * reach, vec31.yCoord * reach, vec31.zCoord * reach).expand(f, f, f),
                    Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
            double d2 = distanceToVec;

            for (Entity entity1 : list) {
                float f1 = entity1.getCollisionBorderSize();
                double kms = HitBoxes.expandHitbox(entity1);
                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand(f1, f1, f1).expand(kms, HitBoxes.vertical.isToggled()? kms : 0, kms);
                MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);
                if (axisalignedbb.isVecInside(vec3)) {
                    if (d2 >= 0.0D) {
                        this.pointedEntity = entity1;
                        vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                        d2 = 0.0D;
                    }
                } else if (movingobjectposition != null) {
                    double d3 = vec3.distanceTo(movingobjectposition.hitVec);
                    if ((d3 < d2) || (d2 == 0.0D))
                        if ((entity1 == entity.ridingEntity)) {
                            if (d2 == 0.0D) {
                                this.pointedEntity = entity1;
                                vec33 = movingobjectposition.hitVec;
                            }
                        } else {
                            this.pointedEntity = entity1;
                            vec33 = movingobjectposition.hitVec;
                            d2 = d3;
                        }
                }
            }

            if ((this.pointedEntity != null) && flag && (vec3.distanceTo(vec33) > (reachMod.isEnabled()? Reach.getReach() : 3.0D))) {
                this.pointedEntity = null;
                assert vec33 != null;
                this.mc.objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec33,
                        null, new BlockPos(vec33));
            }

            if ((this.pointedEntity != null) && ((d2 < distanceToVec) || (this.mc.objectMouseOver == null))) {
                this.mc.objectMouseOver = new MovingObjectPosition(this.pointedEntity, vec33);
                if ((this.pointedEntity instanceof EntityLivingBase) || (this.pointedEntity instanceof EntityItemFrame))
                    this.mc.pointedEntity = this.pointedEntity;
            }

            this.mc.mcProfiler.endSection();
        }
    }
    @Overwrite
    public void updateLightmap(float p_updateLightmap_1_) {
        if (this.lightmapUpdateNeeded) {
            this.mc.mcProfiler.startSection("lightTex");
            World world = this.mc.theWorld;
            Module xray = Raven.moduleManager.getModuleByClazz(Xray.class);
            if (world != null) {

                if (xray.isEnabled()) {
                    for (int i = 0; i < 256; ++i) {
                        this.lightmapColors[i] = 255 << 24 | 255 << 16 | 255 << 8 | 255;
                    }

                    this.lightmapTexture.updateDynamicTexture();
                    this.lightmapUpdateNeeded = false;
                    this.mc.mcProfiler.endSection();

                    return;
                }

                float f = world.getSunBrightness(1.0F);
                float f1 = f * 0.95F + 0.05F;

                for (int i = 0; i < 256; ++i) {
                    float f2 = world.provider.getLightBrightnessTable()[i / 16] * f1;
                    float f3 = world.provider.getLightBrightnessTable()[i % 16] * (this.torchFlickerX * 0.1F + 1.5F);
                    if (world.getLastLightningBolt() > 0) {
                        f2 = world.provider.getLightBrightnessTable()[i / 16];
                    }

                    float f4 = f2 * (f * 0.65F + 0.35F);
                    float f5 = f2 * (f * 0.65F + 0.35F);
                    float f6 = f3 * ((f3 * 0.6F + 0.4F) * 0.6F + 0.4F);
                    float f7 = f3 * (f3 * f3 * 0.6F + 0.4F);
                    float f8 = f4 + f3;
                    float f9 = f5 + f6;
                    float f10 = f2 + f7;
                    f8 = f8 * 0.96F + 0.03F;
                    f9 = f9 * 0.96F + 0.03F;
                    f10 = f10 * 0.96F + 0.03F;
                    float f16;
                    if (this.bossColorModifier > 0.0F) {
                        f16 = this.bossColorModifierPrev + (this.bossColorModifier - this.bossColorModifierPrev) * p_updateLightmap_1_;
                        f8 = f8 * (1.0F - f16) + f8 * 0.7F * f16;
                        f9 = f9 * (1.0F - f16) + f9 * 0.6F * f16;
                        f10 = f10 * (1.0F - f16) + f10 * 0.6F * f16;
                    }

                    if (world.provider.getDimensionId() == 1) {
                        f8 = 0.22F + f3 * 0.75F;
                        f9 = 0.28F + f6 * 0.75F;
                        f10 = 0.25F + f7 * 0.75F;
                    }

                    float f17;
                    if (this.mc.thePlayer.isPotionActive(Potion.nightVision)) {
                        f16 = this.getNightVisionBrightness(this.mc.thePlayer, p_updateLightmap_1_);
                        f17 = 1.0F / f8;
                        if (f17 > 1.0F / f9) {
                            f17 = 1.0F / f9;
                        }

                        if (f17 > 1.0F / f10) {
                            f17 = 1.0F / f10;
                        }

                        f8 = f8 * (1.0F - f16) + f8 * f17 * f16;
                        f9 = f9 * (1.0F - f16) + f9 * f17 * f16;
                        f10 = f10 * (1.0F - f16) + f10 * f17 * f16;
                    }

                    if (f8 > 1.0F) {
                        f8 = 1.0F;
                    }

                    if (f9 > 1.0F) {
                        f9 = 1.0F;
                    }

                    if (f10 > 1.0F) {
                        f10 = 1.0F;
                    }

                    f16 = this.mc.gameSettings.gammaSetting;
                    f17 = 1.0F - f8;
                    float f13 = 1.0F - f9;
                    float f14 = 1.0F - f10;
                    f17 = 1.0F - f17 * f17 * f17 * f17;
                    f13 = 1.0F - f13 * f13 * f13 * f13;
                    f14 = 1.0F - f14 * f14 * f14 * f14;
                    f8 = f8 * (1.0F - f16) + f17 * f16;
                    f9 = f9 * (1.0F - f16) + f13 * f16;
                    f10 = f10 * (1.0F - f16) + f14 * f16;
                    f8 = f8 * 0.96F + 0.03F;
                    f9 = f9 * 0.96F + 0.03F;
                    f10 = f10 * 0.96F + 0.03F;
                    if (f8 > 1.0F) {
                        f8 = 1.0F;
                    }

                    if (f9 > 1.0F) {
                        f9 = 1.0F;
                    }

                    if (f10 > 1.0F) {
                        f10 = 1.0F;
                    }

                    if (f8 < 0.0F) {
                        f8 = 0.0F;
                    }

                    if (f9 < 0.0F) {
                        f9 = 0.0F;
                    }

                    if (f10 < 0.0F) {
                        f10 = 0.0F;
                    }

                    int j = 255;
                    int k = (int) (f8 * 255.0F);
                    int l = (int) (f9 * 255.0F);
                    int i1 = (int) (f10 * 255.0F);
                    this.lightmapColors[i] = j << 24 | k << 16 | l << 8 | i1;
                }

                this.lightmapTexture.updateDynamicTexture();
                this.lightmapUpdateNeeded = false;
                this.mc.mcProfiler.endSection();
            }
        }
    }
}
