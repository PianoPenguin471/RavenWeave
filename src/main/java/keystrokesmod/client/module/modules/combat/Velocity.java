package keystrokesmod.client.module.modules.combat;

import club.maxstats.weave.loader.api.event.EventBus;
import club.maxstats.weave.loader.api.event.RenderGameOverlayEvent;
import club.maxstats.weave.loader.api.event.SubscribeEvent;
import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.setting.impl.ComboSetting;
import keystrokesmod.client.module.setting.impl.SliderSetting;
import keystrokesmod.client.module.setting.impl.TickSetting;
import keystrokesmod.client.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
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

    @SubscribeEvent
    public void onLivingUpdate(RenderGameOverlayEvent fe) {
        System.out.println("PLEASE");
        if (Utils.Player.isPlayerInGame() && mc.thePlayer.maxHurtTime > 0
                && mc.thePlayer.hurtTime == mc.thePlayer.maxHurtTime) {
            System.out.println("First velo condition");
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
                    velo();
                    return;
                } else if (attacker.getDistanceToEntity(mc.thePlayer) > distanceProjectiles.getInput()) {
                    velo();
                    return;
                }
            }

            if (chance.getInput() != 100.0D) {
                double ch = Math.random();
                if (ch >= chance.getInput() / 100.0D) {
                    return;
                }
            }

            if (horizontal.getInput() != 100.0D) {
                mc.thePlayer.motionX *= horizontal.getInput() / 100.0D;
                mc.thePlayer.motionZ *= horizontal.getInput() / 100.0D;
            }

            if (vertical.getInput() != 100.0D) {
                mc.thePlayer.motionY *= vertical.getInput() / 100.0D;
            }
        }
    }

    public void velo() {
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
