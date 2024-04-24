package me.pianopenguin471.mixins;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.potion.Potion;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import ravenweave.client.Raven;
import ravenweave.client.hook.finder.Finder$EntityRenderer$getNightVisionBrightness;
import ravenweave.client.module.Module;
import ravenweave.client.module.modules.render.Xray;
import ravenweave.client.util.MappingsWorkAround;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class EntityRendererMixinHelper {
    public static List<Entity> getAroundGoogleRelocation1(Entity entity, Vec3 vec31, double reach, float f) {
        DummyPredicate canBeCollidedWith = new DummyPredicate();
        return (List<Entity>) DummyPredicate.workAround(entity, vec31, reach, f, canBeCollidedWith);
    }
    
    public static void updateLightmap(Object instance, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();

        if (((EntityRendererAccessor) instance).getLightmapUpdateNeeded()) {
            mc.mcProfiler.startSection("lightTex");
            World world = mc.theWorld;
            Module xray = Raven.moduleManager.getModuleByClazz(Xray.class);
            if (world != null) {

                if (xray.isEnabled()) {
                    for (int i = 0; i < 256; ++i) {
                        ((EntityRendererAccessor) instance).getLightmapColors()[i] = 255 << 24 | 255 << 16 | 255 << 8 | 255;
                    }

                    ((EntityRendererAccessor) instance).getLightmapTexture().updateDynamicTexture();
                    ((EntityRendererAccessor) instance).setLightmapUpdateNeeded(false);
                    mc.mcProfiler.endSection();

                    return;
                }

                float f = world.getSunBrightness(1.0F);
                float f1 = f * 0.95F + 0.05F;

                for (int i = 0; i < 256; ++i) {
                    float f2 = world.provider.getLightBrightnessTable()[i / 16] * f1;
                    float f3 = world.provider.getLightBrightnessTable()[i % 16] * (((EntityRendererAccessor) instance).getTorchFlickerX() * 0.1F + 1.5F);
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
                    if (((EntityRendererAccessor) instance).getBossColorModifier() > 0.0F) {
                        f16 = ((EntityRendererAccessor) instance).getBossColorModifierPrev() + (((EntityRendererAccessor) instance).getBossColorModifier() - ((EntityRendererAccessor) instance).getBossColorModifierPrev()) * partialTicks;
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
                    if (mc.thePlayer.isPotionActive(Potion.nightVision)) {
                        try {
                            f16 = (float) MappingsWorkAround.findMethod(instance.getClass(), Finder$EntityRenderer$getNightVisionBrightness.ID).invoke(instance, mc.thePlayer, partialTicks);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
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

                    f16 = mc.gameSettings.gammaSetting;
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
                    ((EntityRendererAccessor) instance).getLightmapColors()[i] = j << 24 | k << 16 | l << 8 | i1;

                }

                ((EntityRendererAccessor) instance).getLightmapTexture().updateDynamicTexture();
                ((EntityRendererAccessor) instance).setLightmapUpdateNeeded(false);
                mc.mcProfiler.endSection();
            }
        }
    }
}

class DummyPredicate implements Predicate<Entity> {
    @Override
    public boolean apply(Entity entity) {
        return entity.canBeCollidedWith();
    }

    public static Object workAround(Entity entity, Vec3 vec31, double reach, float f, DummyPredicate canBeCollidedWith) {
        return Minecraft.getMinecraft().theWorld.getEntitiesInAABBexcluding(entity,
                entity.getEntityBoundingBox()
                        .addCoord(vec31.xCoord * reach, vec31.yCoord * reach, vec31.zCoord * reach).expand(f, f, f),
                Predicates.and(EntitySelectors.NOT_SPECTATING, canBeCollidedWith));
    }
}
