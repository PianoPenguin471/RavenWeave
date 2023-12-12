package ravenweave.client.module.modules.aycy.optimalaim.f;

import net.minecraft.entity.player.EntityPlayer;
import net.weavemc.loader.api.event.RenderLivingEvent;
import ravenweave.client.module.modules.aycy.optimalaim.OptimalAim;
import ravenweave.client.module.modules.aycy.optimalaim.e.BoundingBoxWrapper;

public class PlayerPosWrapper {
    private final RenderLivingEvent renderPlayerEvent;
    private final double radius;
    private final double eyeHeight;
    private final double horizontalCenterOffset;
    private final double width;
    private final double height;
    private final double x;
    private final double y;
    private final double z;

    public PlayerPosWrapper(RenderLivingEvent a, EntityPlayer b) {
        this.renderPlayerEvent = a;
        this.eyeHeight = (double)b.getEyeHeight();
        this.radius = OptimalAim.radius.getInput();
        this.horizontalCenterOffset = (double)(b.width / 2.0F + 0.1F);
        this.width = (double)(b.width + 0.2F);
        this.height = (double)(b.height + 0.2F);
        this.x = b.lastTickPosX + (b.posX - b.lastTickPosX) * (double)a.getPartialTicks();
        this.y = b.lastTickPosY + (b.posY - b.lastTickPosY) * (double)a.getPartialTicks();
        this.z = b.lastTickPosZ + (b.posZ - b.lastTickPosZ) * (double)a.getPartialTicks();
    }

    public BoundingBoxWrapper getBoundingBox(PlayerPosWrapper a) {
        double b = this.a(this.renderPlayerEvent.getX() - a.x, this.x, a.x - this.horizontalCenterOffset, this.width, this.radius);
        double c = this.a(this.renderPlayerEvent.getY() - a.y, this.y + this.eyeHeight, a.y - 0.10000000149011612, this.height, this.radius);
        double d = this.a(this.renderPlayerEvent.getZ() - a.z, this.z, a.z - this.horizontalCenterOffset, this.width, this.radius);
        return new BoundingBoxWrapper(b - this.radius, c - this.radius, d - this.radius, b + this.radius, c + this.radius, d + this.radius);
    }

    private double a(double a, double b, double c, double d, double e) {
        if (b <= c + e) {
            return c + a + e;
        } else {
            return b >= c + d - e ? c + d + a - e : b + a;
        }
    }
}
