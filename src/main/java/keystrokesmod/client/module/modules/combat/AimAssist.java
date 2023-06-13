package keystrokesmod.client.module.modules.combat;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import com.google.common.eventbus.Subscribe;
import keystrokesmod.client.command.commands.Friends;
import keystrokesmod.client.event.impl.TickEvent;
import keystrokesmod.client.module.modules.world.AntiBot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Mouse;

import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.modules.client.Targets;
import keystrokesmod.client.module.setting.impl.DescriptionSetting;
import keystrokesmod.client.module.setting.impl.SliderSetting;
import keystrokesmod.client.module.setting.impl.TickSetting;
import keystrokesmod.client.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class AimAssist extends Module {

    public static SliderSetting speedYaw, complimentYaw, speedPitch, complimentPitch;
    public static SliderSetting fov;
    public static SliderSetting distance;
    public static SliderSetting pitchOffSet;
    public static TickSetting clickAim;
    public static TickSetting stopWhenOver;
    public static TickSetting aimPitch;
    public static TickSetting weaponOnly;
    public static TickSetting aimInvis;
    public static TickSetting breakBlocks;
    public static TickSetting blatantMode;
    public static TickSetting aimWhileTargeting;

    public AimAssist() {
        super("AimAssist", ModuleCategory.combat);
        this.registerSetting(new DescriptionSetting("Set targets in Client->Targets"));
        this.registerSetting(speedYaw = new SliderSetting("Speed 1 (yaw)", 45.0D, 5.0D, 100.0D, 1.0D));
        this.registerSetting(complimentYaw = new SliderSetting("Speed 2 (yaw)", 15.0D, 2D, 97.0D, 1.0D));
        this.registerSetting(speedPitch = new SliderSetting("Speed 1 (pitch)", 45.0D, 5.0D, 100.0D, 1.0D));
        this.registerSetting(complimentPitch = new SliderSetting("Speed 2 (pitch)", 15.0D, 2D, 97.0D, 1.0D));
        this.registerSetting(pitchOffSet = new SliderSetting("pitchOffSet (blocks)", 4D, -2, 2, 0.050D));
        this.registerSetting(clickAim = new TickSetting("Click aim", true));
        this.registerSetting(breakBlocks = new TickSetting("Break blocks", true));
        this.registerSetting(weaponOnly = new TickSetting("Weapon only", false));
        this.registerSetting(blatantMode = new TickSetting("Blatant mode", false));
        this.registerSetting(aimPitch = new TickSetting("Aim pitch", false));
        this.registerSetting(aimWhileTargeting = new TickSetting("Aim while targeting", true));
    }

    public boolean isLookingAtPlayer() {
        if (mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) return false;
        if (!(mc.objectMouseOver.entityHit instanceof EntityPlayer)) return false;
        return !AntiBot.bot(mc.objectMouseOver.entityHit);
    }

    @Subscribe
    public void onRender(TickEvent fe) {
        try {
            if (!Utils.Client.currentScreenMinecraft() || !Utils.Player.isPlayerInGame())
                return;

            if (breakBlocks.isToggled() && (mc.objectMouseOver != null)) {
                BlockPos p = mc.objectMouseOver.getBlockPos();
                if (p != null) {
                    Block bl = mc.theWorld.getBlockState(p).getBlock();
                    if ((bl != Blocks.air) && !(bl instanceof BlockLiquid) && (bl != null))
                        return;
                }
            }

            if (!weaponOnly.isToggled() || Utils.Player.isPlayerHoldingWeapon()) {
                boolean shouldAim = !clickAim.isToggled() || Mouse.isButtonDown(0) || (clickAim.isToggled() && Utils.Client.autoClickerClicking());
                if (shouldAim) {
                    if (isLookingAtPlayer() && !aimWhileTargeting.isToggled()) return;

                    Entity en = Friends.getEnemy();
                    if (en != null) {
                        if (blatantMode.isToggled())
                            Utils.Player.aim(en, (float) pitchOffSet.getInput());
                        else {
                            double n = Utils.Player.fovFromEntity(en);
                            if ((n > 1.0D) || (n < -1.0D)) {
                                double complimentSpeed = n
                                        * (ThreadLocalRandom.current().nextDouble(complimentYaw.getInput() - 1.47328,
                                        complimentYaw.getInput() + 2.48293) / 100);
                                float val = (float) (-(complimentSpeed + (n / (101.0D - (float) ThreadLocalRandom.current()
                                        .nextDouble(speedYaw.getInput() - 4.723847, speedYaw.getInput())))));
                                mc.thePlayer.rotationYaw += val;
                            }
                            if (aimPitch.isToggled()) {
                                double complimentSpeed = Utils.Player.PitchFromEntity(en,
                                        (float) pitchOffSet.getInput())
                                        * (ThreadLocalRandom.current().nextDouble(complimentPitch.getInput() - 1.47328,
                                        complimentPitch.getInput() + 2.48293) / 100);

                                float val = (float) (-(complimentSpeed
                                        + (n / (101.0D - (float) ThreadLocalRandom.current()
                                        .nextDouble(speedPitch.getInput() - 4.723847,
                                                speedPitch.getInput())))));

                                mc.thePlayer.rotationPitch += val;
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}