package ravenweave.client.module.modules.combat;

import me.pianopenguin471.mixins.S12PacketEntityVelocityAccessor;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.weavemc.loader.api.event.SubscribeEvent;
import ravenweave.client.event.impl.PacketEvent;
import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.ComboSetting;
import ravenweave.client.module.setting.impl.SliderSetting;
import ravenweave.client.module.setting.impl.TickSetting;

public class Velocity extends Module {
    public static SliderSetting horizontal, vertical, chance, horizontalProjectiles, verticalProjectiles, chanceProjectiles, distanceProjectiles;
    public static TickSetting invertHorizontal, invertVertical;
    public static TickSetting onlyWhileTargeting, disableWhileHoldingS, differentVeloForProjectiles;
    public static ComboSetting<Mode> projectilesMode;
    public Mode mode = Mode.Distance;

    public Velocity() {
        super("Velocity", ModuleCategory.combat);
        this.registerSetting(horizontal = new SliderSetting("Horizontal", 90.0D, 0.0D, 200.0D, 1.0D));
        this.registerSetting(invertHorizontal = new TickSetting("Invert Horizontal", false));
        this.registerSetting(vertical = new SliderSetting("Vertical", 100.0D, 0.0D, 200.0D, 1.0D));
        this.registerSetting(invertVertical= new TickSetting("Invert Vertical", false));
        this.registerSetting(chance = new SliderSetting("Chance", 100.0D, 0.0D, 100.0D, 1.0D));
        this.registerSetting(onlyWhileTargeting = new TickSetting("Only while targeting", false));
        this.registerSetting(disableWhileHoldingS = new TickSetting("Disable while holding S", false));
        this.registerSetting(differentVeloForProjectiles = new TickSetting("Different velo for projectiles", false));
        this.registerSetting(projectilesMode = new ComboSetting<>("Projectiles Mode", mode));
        this.registerSetting(horizontalProjectiles = new SliderSetting("Horizontal projectiles", 90.0D, -100.0D, 100.0D, 1.0D));
        this.registerSetting(verticalProjectiles = new SliderSetting("Vertical projectiles", 100.0D, -100.0D, 100.0D, 1.0D));
        this.registerSetting(chanceProjectiles = new SliderSetting("Chance projectiles", 100.0D, 0.0D, 100.0D, 1.0D));
        this.registerSetting(distanceProjectiles = new SliderSetting("Distance projectiles", 3D, 0.0D, 20D, 0.1D));
    }

    @SubscribeEvent
    public void onPacket(PacketEvent packetEvent) {
        try {
            if (!packetEvent.isIncoming()) return;
            if (!(packetEvent.getPacket() instanceof S12PacketEntityVelocity)) return;
            if (chance.getInput() != 100.0D) {
                double ch = Math.random() * 100;
                if (ch >= chance.getInput()) {
                    return;
                }
            }
            velo(packetEvent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void velo(PacketEvent packetEvent) {
        S12PacketEntityVelocity packet = packetEvent.getPacket();
        S12PacketEntityVelocityAccessor accessorPacket = (S12PacketEntityVelocityAccessor) packet;

        if (invertHorizontal.isToggled()) {
            accessorPacket.setMotionX((int) (packet.getMotionX() * -horizontal.getInput()/100));
            accessorPacket.setMotionZ((int) (packet.getMotionZ() * -horizontal.getInput()/100));
        } else {
            accessorPacket.setMotionX((int) (packet.getMotionX() * horizontal.getInput()/100));
            accessorPacket.setMotionZ((int) (packet.getMotionZ() * horizontal.getInput()/100));
        }

        if (invertVertical.isToggled()) accessorPacket.setMotionY((int) (packet.getMotionY() * -vertical.getInput()/100));
        else accessorPacket.setMotionY((int) (packet.getMotionY() * vertical.getInput()/100));

        packetEvent.setPacket(accessorPacket);
    }

    public enum Mode {
        Distance, ItemHeld
    }
}
