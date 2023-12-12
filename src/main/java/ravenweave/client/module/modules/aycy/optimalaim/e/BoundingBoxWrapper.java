package ravenweave.client.module.modules.aycy.optimalaim.e;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public class BoundingBoxWrapper extends AxisAlignedBB {
    public BoundingBoxWrapper(double a, double b, double c, double d, double e, double f) {
        super(a, b, c, d, e, f);
    }

    public Vec3 getCenter() {
        return new Vec3((maxX + minX)/2, (maxY + minY)/2, (maxZ + minZ)/2);
    }
}