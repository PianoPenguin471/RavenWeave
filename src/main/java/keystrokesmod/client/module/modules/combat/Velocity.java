package keystrokesmod.client.module.modules.combat;

import club.maxstats.weave.loader.api.event.RenderGameOverlayEvent;
import club.maxstats.weave.loader.api.event.SubscribeEvent;
import com.google.common.eventbus.Subscribe;
import keystrokesmod.client.event.impl.PacketEvent;
import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.setting.impl.ComboSetting;
import keystrokesmod.client.module.setting.impl.SliderSetting;
import keystrokesmod.client.module.setting.impl.TickSetting;
import keystrokesmod.client.utils.Utils;
import me.PianoPenguin471.mixins.S12PacketEntityVelocityAccessor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.ChatComponentText;
import org.lwjgl.input.Keyboard;

public class Velocity extends Module {
    public static SliderSetting horizontal, vertical, chance, horizontalProjectiles, verticalProjectiles, chanceProjectiles, distanceProjectiles;
    public static TickSetting onlyWhileTargeting, disableWhileHoldingS, differentVeloForProjectiles;
    public static ComboSetting projectilesMode;
    public Mode mode = Mode.Distance;

    public Velocity() {
        super("Velocity", ModuleCategory.combat);
        this.registerSetting(horizontal = new SliderSetting("Horizontal", 90.0D, -100.0D, 100.0D, 1.0D));
        this.registerSetting(vertical = new SliderSetting("Vertical", 100.0D, -100.0D, 100.0D, 1.0D));
        this.registerSetting(chance = new SliderSetting("Chance", 100.0D, 0.0D, 100.0D, 1.0D));
        this.registerSetting(onlyWhileTargeting = new TickSetting("Only while targeting", false));
        this.registerSetting(disableWhileHoldingS = new TickSetting("Disable while holding S", false));
        this.registerSetting(differentVeloForProjectiles = new TickSetting("Different velo for projectiles", false));
        this.registerSetting(projectilesMode = new ComboSetting("Projectiles Mode", mode));
        this.registerSetting(horizontalProjectiles = new SliderSetting("Horizontal projectiles", 90.0D, -100.0D, 100.0D, 1.0D));
        this.registerSetting(verticalProjectiles = new SliderSetting("Vertical projectiles", 100.0D, -100.0D, 100.0D, 1.0D));
        this.registerSetting(chanceProjectiles = new SliderSetting("Chance projectiles", 100.0D, 0.0D, 100.0D, 1.0D));
        this.registerSetting(distanceProjectiles = new SliderSetting("Distance projectiles", 3D, 0.0D, 20D, 0.1D));
    }

    @Subscribe
    public void onPacket(PacketEvent packetEvent) {
        mc.thePlayer.addChatMessage(new ChatComponentText("Packet event called"));
        if (!packetEvent.isIncoming()) return;
        if (!(packetEvent.getPacket() instanceof S12PacketEntityVelocity)) return;
        if (chance.getInput() != 100.0D) {
            double ch = Math.random() * 100;
            if (ch >= chance.getInput()) {
                return;
            }
        }

        if (onlyWhileTargeting.isToggled() && (mc.objectMouseOver == null || mc.objectMouseOver.entityHit == null)) {
            return;
        }

        if (disableWhileHoldingS.isToggled() && Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())) {
            return;
        }

        if (mc.thePlayer.getLastAttacker() instanceof EntityPlayer) {
            EntityPlayer attacker = (EntityPlayer) mc.thePlayer.getLastAttacker();
            Item item = attacker.getCurrentEquippedItem() != null ? attacker.getCurrentEquippedItem().getItem()
                    : null;
            if ((item instanceof ItemEgg || item instanceof ItemBow || item instanceof ItemSnow
                    || item instanceof ItemFishingRod) && mode == Mode.ItemHeld) {
                velo(packetEvent);
            } else if (attacker.getDistanceToEntity(mc.thePlayer) > distanceProjectiles.getInput()) {
                velo(packetEvent);
            }
        }
    }

    public void velo(PacketEvent packetEvent) {
        S12PacketEntityVelocity packet = packetEvent.getPacket();

        if (packet.getEntityID() != mc.thePlayer.getEntityId()) return;

        S12PacketEntityVelocityAccessor accessorPacket = (S12PacketEntityVelocityAccessor) packet;
        accessorPacket.setMotionX((int) (packet.getMotionX() * horizontal.getInput() / 100));
        accessorPacket.setMotionZ((int) (packet.getMotionZ() * horizontal.getInput() / 100));
        accessorPacket.setMotionY((int) (packet.getMotionY() * vertical.getInput() / 100));




        System.out.println("Calculating Velo");
        if (chanceProjectiles.getInput() != 100.0D) {
            double ch = Math.random();
            if (ch >= chanceProjectiles.getInput() / 100.0D) {
                return;
            }
        }

        if (horizontalProjectiles.getInput() != 100.0D) {
            mc.thePlayer.motionX *= horizontalProjectiles.getInput() / 100.0D;
            mc.thePlayer.motionZ *= horizontalProjectiles.getInput() / 100.0D;
        }

        if (verticalProjectiles.getInput() != 100.0D) {
            mc.thePlayer.motionY *= verticalProjectiles.getInput() / 100.0D;
        }
    }

    public enum Mode {
        Distance, ItemHeld
    }
}
