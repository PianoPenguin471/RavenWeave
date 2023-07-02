package ravenweave.client.module.modules.combat;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.opengl.GL11;
import ravenweave.client.main.Raven;
import ravenweave.client.module.Module;
import ravenweave.client.module.modules.world.AntiBot;
import ravenweave.client.module.setting.impl.DescriptionSetting;
import ravenweave.client.module.setting.impl.SliderSetting;
import ravenweave.client.module.setting.impl.TickSetting;
import ravenweave.client.utils.Utils;

import java.awt.*;

public class HitBoxes extends Module {
    public static SliderSetting distance;
    public static TickSetting vertical;

    public HitBoxes() {
        super("HitBoxes", ModuleCategory.combat);
        this.registerSetting(new DescriptionSetting("Changed from multiplier to extra blocks!"));
        this.registerSetting(distance = new SliderSetting("Extra Blocks", 0.2D, 0.05D, 2.0D, 0.05D));
        this.registerSetting(vertical = new TickSetting("Vertical", false));
    }

    public static double exp(Entity en) {
        Module hitBox = Raven.moduleManager.getModuleByClazz(HitBoxes.class);
        return ((hitBox != null) && hitBox.isEnabled() && !AntiBot.bot(en)) ? distance.getInput() : 0D;
    }

    private void rh(Entity e, Color c) {
        if (e instanceof EntityLivingBase) {
            double x = (e.lastTickPosX + ((e.posX - e.lastTickPosX) * (double) Utils.Client.getTimer().renderPartialTicks))
                    - mc.getRenderManager().viewerPosX;
            double y = (e.lastTickPosY + ((e.posY - e.lastTickPosY) * (double) Utils.Client.getTimer().renderPartialTicks))
                    - mc.getRenderManager().viewerPosY;
            double z = (e.lastTickPosZ + ((e.posZ - e.lastTickPosZ) * (double) Utils.Client.getTimer().renderPartialTicks))
                    - mc.getRenderManager().viewerPosZ;
            float ex = (float) ((double) e.getCollisionBorderSize() * distance.getInput());
            AxisAlignedBB bbox = e.getEntityBoundingBox().expand(ex, ex, ex);
            AxisAlignedBB axis = new AxisAlignedBB((bbox.minX - e.posX) + x, (bbox.minY - e.posY) + y,
                    (bbox.minZ - e.posZ) + z, (bbox.maxX - e.posX) + x, (bbox.maxY - e.posY) + y, (bbox.maxZ - e.posZ) + z);
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(3042);
            GL11.glDisable(3553);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            GL11.glLineWidth(2.0F);
            GL11.glColor3d(c.getRed(), c.getGreen(), c.getBlue());
            RenderGlobal.drawSelectionBoundingBox(axis);
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glDisable(3042);
        }
    }
}