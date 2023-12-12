package ravenweave.client.module.modules.aycy.optimalaim.f;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import ravenweave.client.module.modules.aycy.optimalaim.e.BoundingBoxWrapper;

public class RenderUtils {
    private static final Tessellator tessellator = Tessellator.getInstance();
    private static final WorldRenderer worldRenderer;

    public RenderUtils() {
    }

    public static void drawBoundingBox(BoundingBoxWrapper a) {
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(a.minX, a.maxY, a.maxZ).endVertex();
        worldRenderer.pos(a.maxX, a.maxY, a.maxZ).endVertex();
        worldRenderer.pos(a.maxX, a.maxY, a.minZ).endVertex();
        worldRenderer.pos(a.minX, a.maxY, a.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(a.maxX, a.minY, a.maxZ).endVertex();
        worldRenderer.pos(a.minX, a.minY, a.maxZ).endVertex();
        worldRenderer.pos(a.minX, a.minY, a.minZ).endVertex();
        worldRenderer.pos(a.maxX, a.minY, a.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(a.minX, a.maxY, a.minZ).endVertex();
        worldRenderer.pos(a.maxX, a.maxY, a.minZ).endVertex();
        worldRenderer.pos(a.maxX, a.minY, a.minZ).endVertex();
        worldRenderer.pos(a.minX, a.minY, a.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(a.maxX, a.maxY, a.maxZ).endVertex();
        worldRenderer.pos(a.minX, a.maxY, a.maxZ).endVertex();
        worldRenderer.pos(a.minX, a.minY, a.maxZ).endVertex();
        worldRenderer.pos(a.maxX, a.minY, a.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(a.minX, a.maxY, a.maxZ).endVertex();
        worldRenderer.pos(a.minX, a.maxY, a.minZ).endVertex();
        worldRenderer.pos(a.minX, a.minY, a.minZ).endVertex();
        worldRenderer.pos(a.minX, a.minY, a.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(a.maxX, a.maxY, a.minZ).endVertex();
        worldRenderer.pos(a.maxX, a.maxY, a.maxZ).endVertex();
        worldRenderer.pos(a.maxX, a.minY, a.maxZ).endVertex();
        worldRenderer.pos(a.maxX, a.minY, a.minZ).endVertex();
        tessellator.draw();
    }

    public static void a(int a, int b, int c, int d, int e, int f) {
        float g = (float)(e >> 24 & 255) / 255.0F;
        float h = (float)(e >> 16 & 255) / 255.0F;
        float i = (float)(e >> 8 & 255) / 255.0F;
        float j = (float)(e & 255) / 255.0F;
        float k = (float)(f >> 24 & 255) / 255.0F;
        float l = (float)(f >> 16 & 255) / 255.0F;
        float m = (float)(f >> 8 & 255) / 255.0F;
        float n = (float)(f & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos((double)a, (double)b, 0.0).color(h, i, j, g).endVertex();
        worldRenderer.pos((double)a, (double)d, 0.0).color(h, i, j, g).endVertex();
        worldRenderer.pos((double)c, (double)d, 0.0).color(l, m, n, k).endVertex();
        worldRenderer.pos((double)c, (double)b, 0.0).color(l, m, n, k).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    static {
        worldRenderer = tessellator.getWorldRenderer();
    }
}
