package ravenweave.client.module.modules.beta;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.util.*;
import net.weavemc.loader.api.event.RenderGameOverlayEvent;
import net.weavemc.loader.api.event.SubscribeEvent;
import net.weavemc.loader.api.event.TickEvent;
import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.DescriptionSetting;
import ravenweave.client.module.setting.impl.SliderSetting;
import ravenweave.client.utils.CaterpillowUtils;

public class TellyBridge extends Module {
    public static SliderSetting bufferDistance;
    public TellyBridge() {
        super("TellyBridge", ModuleCategory.beta);
        this.registerSetting(new DescriptionSetting("Courtesy of Caterpillow"));
        this.registerSetting(bufferDistance = new SliderSetting("Buffer distance", 2.5, 0, 10, 0.5));
    }
    private Phase phase = Phase.PreJump;
    private float startYaw;
    private float startPitch;
    private BlockPos lastBlock;

    public void onEnable() {
        if (mc.inGameHasFocus) {
            this.startYaw = mc.thePlayer.rotationYaw;
            this.startPitch = mc.thePlayer.rotationPitch;
            this.phase = Phase.PreJump;
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
            this.lastBlock = CaterpillowUtils.isAirBlock(new BlockPos(mc.thePlayer).down()) ? null : new BlockPos(mc.thePlayer).down();
        } else {
            this.disable();
        }
    }

    public void onDisable() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (this.phase.equals(Phase.Placing)) {
            this.placeBlock((int)mc.playerController.getBlockReachDistance(), false, event.getPartialTicks());
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (this.lastBlock == null) {
            mc.thePlayer.addChatMessage(new ChatComponentText("§cError: no blocks nearby found"));
            this.toggle();
            return;
        }
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
        if (Math.sqrt(mc.thePlayer.getDistanceSq(this.lastBlock)) > bufferDistance.getInput()) {
            this.phase = Phase.Placing;
        } else if (!CaterpillowUtils.isAirBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.2, mc.thePlayer.posZ)) && this.phase.equals(Phase.Placing)) {
            this.phase = Phase.Turn;
        } else if (mc.thePlayer.isCollidedVertically && this.phase.equals(Phase.Turn)) {
            this.phase = Phase.Jump;
        }
        switch (this.phase) {
            case Turn: {
                mc.thePlayer.rotationYaw = this.startYaw;
                mc.thePlayer.rotationPitch = this.startPitch;
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
            }
            case PreJump: {
                mc.thePlayer.rotationYaw = this.startYaw;
                mc.thePlayer.rotationPitch = this.startPitch;
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
                this.phase = Phase.Jump;
                break;
            }
            case Jump: {
                mc.thePlayer.rotationYaw = this.startYaw;
                mc.thePlayer.rotationPitch = this.startPitch;
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), true);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
                this.phase = Phase.Jumping;
                break;
            }
            case Jumping: {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), true);
                break;
            }
            case Placing: {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), true);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
                this.placeBlock((int)mc.playerController.getBlockReachDistance(), true, 0.0f);
            }
        }
    }

    public void placeBlock(int range, boolean place, float partialTicks) {
        if (!CaterpillowUtils.isAirBlock(new BlockPos(mc.thePlayer).down())) {
            return;
        }
        if (this.placeBlockSimple(new BlockPos(mc.thePlayer).down(), place, partialTicks)) {
            if (place) {
                this.lastBlock = new BlockPos(mc.thePlayer).down();
            }
            return;
        }
        for (int dist = 0; dist <= range; ++dist) {
            for (int blockDist = 0; dist != blockDist; ++blockDist) {
                for (int x = blockDist; x >= 0; --x) {
                    int z = blockDist - x;
                    int y = dist - blockDist;
                    if (this.placeBlockSimple(new BlockPos(mc.thePlayer).down(y).north(x).west(z), place, partialTicks)) {
                        return;
                    }
                    if (this.placeBlockSimple(new BlockPos(mc.thePlayer).down(y).north(x).west(-z), place, partialTicks)) {
                        return;
                    }
                    if (this.placeBlockSimple(new BlockPos(mc.thePlayer).down(y).north(-x).west(z), place, partialTicks)) {
                        return;
                    }
                    if (!this.placeBlockSimple(new BlockPos(mc.thePlayer).down(y).north(-x).west(-z), place, partialTicks)) continue;
                    return;
                }
            }
        }
    }



    public boolean placeBlockSimple(BlockPos pos, boolean place, float partialTicks) {
        Entity entity = mc.getRenderViewEntity();
        double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
        double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
        double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
        Vec3 eyesPos = new Vec3(d0, d1 + (double)mc.thePlayer.getEyeHeight(), d2);
        for (EnumFacing side : EnumFacing.values()) {
            Vec3 hitVec;
            if (side.equals(EnumFacing.UP) || side.equals(EnumFacing.DOWN)) continue;
            BlockPos neighbor = pos.offset(side);
            EnumFacing side2 = side.getOpposite();
            if (!CaterpillowUtils.getBlock(neighbor).canCollideCheck(mc.theWorld.getBlockState(neighbor), false) || eyesPos.squareDistanceTo(hitVec = new Vec3(neighbor).addVector(0.5, 0.5, 0.5).add(new Vec3(side2.getDirectionVec()))) > 36.0) continue;
            float[] angles = CaterpillowUtils.getRotations(neighbor, side2, partialTicks);
            mc.getRenderViewEntity().rotationYaw = angles[0];
            mc.getRenderViewEntity().rotationPitch = angles[1];
            if (place) {
                mc.thePlayer.rotationYaw = angles[0];
                mc.thePlayer.rotationPitch = angles[1];
                mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), neighbor, side2, hitVec);
                mc.thePlayer.swingItem();
                this.lastBlock = pos;
            }
            return true;
        }
        return false;
    }

    enum Phase {
        PreJump,
        Turn,
        Jump,
        Jumping,
        Placing;
    }
}
