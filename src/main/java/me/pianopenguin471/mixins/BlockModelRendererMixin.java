package me.pianopenguin471.mixins;

import ravenweave.client.module.modules.render.Xray;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ReportedException;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import ravenweave.client.Raven;
@Mixin(value = BlockModelRenderer.class, priority = 999)
public abstract class BlockModelRendererMixin {

    @Shadow public abstract boolean renderModelAmbientOcclusion(IBlockAccess p_renderModelAmbientOcclusion_1_, IBakedModel p_renderModelAmbientOcclusion_2_, Block p_renderModelAmbientOcclusion_3_, BlockPos p_renderModelAmbientOcclusion_4_, WorldRenderer p_renderModelAmbientOcclusion_5_, boolean p_renderModelAmbientOcclusion_6_);

    @Shadow public abstract boolean renderModelStandard(IBlockAccess p_renderModelStandard_1_, IBakedModel p_renderModelStandard_2_, Block p_renderModelStandard_3_, BlockPos p_renderModelStandard_4_, WorldRenderer p_renderModelStandard_5_, boolean p_renderModelStandard_6_);

    /**
     * @author mc code
     * @reason god help me
     */
    @Overwrite
    public boolean renderModel(IBlockAccess p_renderModel_1_, IBakedModel p_renderModel_2_, IBlockState p_renderModel_3_, BlockPos p_renderModel_4_, WorldRenderer p_renderModel_5_, boolean p_renderModel_6_) {
        boolean flag = Minecraft.isAmbientOcclusionEnabled() && p_renderModel_3_.getBlock().getLightValue() == 0 && p_renderModel_2_.isAmbientOcclusion() || Raven.moduleManager.getModuleByClazz(Xray.class).isEnabled();

        try {
            Block block = p_renderModel_3_.getBlock();
            return flag ? this.renderModelAmbientOcclusion(p_renderModel_1_, p_renderModel_2_, block, p_renderModel_4_, p_renderModel_5_, p_renderModel_6_) : this.renderModelStandard(p_renderModel_1_, p_renderModel_2_, block, p_renderModel_4_, p_renderModel_5_, p_renderModel_6_);
        } catch (Throwable var11) {
            CrashReport crashreport = CrashReport.makeCrashReport(var11, "Tesselating block model");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block model being tesselated");
            CrashReportCategory.addBlockInfo(crashreportcategory, p_renderModel_4_, p_renderModel_3_);
            crashreportcategory.addCrashSection("Using AO", flag);
            throw new ReportedException(crashreport);
        }
    }
}