package ravenweave.client.utils;

import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import static ravenweave.client.main.Raven.mc;

public class RayTraceUtils {
    // This method was protected in the Entity class, so I pasted it
    public static Vec3 getVectorForRotation(float pitch, float yaw) {
        float var3 = MathHelper.cos(-yaw * 0.017453292F - 3.1415927F);
        float var4 = MathHelper.sin(-yaw * 0.017453292F - 3.1415927F);
        float var5 = -MathHelper.cos(-pitch * 0.017453292F);
        float var6 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3((double)(var4 * var5), (double)var6, (double)(var3 * var5));
    }

    // Modified from Entity#rayTrace to use custom angles
    public static MovingObjectPosition customRayTrace(double reach, float pitch, float yaw) {
        Vec3 origin = mc.getRenderViewEntity().getPositionEyes(1.0F);
        Vec3 angleVec = getVectorForRotation(pitch, yaw);
        Vec3 end = origin.addVector(angleVec.xCoord * reach, angleVec.yCoord * reach, angleVec.zCoord * reach);
        return mc.theWorld.rayTraceBlocks(origin, end, false, false, true);
    }
}
