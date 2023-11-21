package ravenweave.client.module.modules.beta;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.weavemc.loader.api.event.RenderGameOverlayEvent;
import net.weavemc.loader.api.event.SubscribeEvent;
import net.weavemc.loader.api.event.TickEvent;
import org.lwjgl.input.Keyboard;
import ravenweave.client.event.LookEvent;
import ravenweave.client.event.UpdateEvent;
import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.DescriptionSetting;
import ravenweave.client.module.setting.impl.SliderSetting;
import ravenweave.client.module.setting.impl.TickSetting;
import ravenweave.client.utils.CaterpillowUtils;
import ravenweave.client.utils.Utils;

public class AutoClutch extends Module {
    public static SliderSetting range;
    public static TickSetting cpsCap, clientRots;
    private float serverYaw, serverPitch;
    public AutoClutch() {
        super("AutoClutch", ModuleCategory.player);
        this.registerSetting(new DescriptionSetting("Block clutches for you"));
        this.registerSetting(new DescriptionSetting("Do not use on servers. Does not bypass"));
        this.registerSetting(new DescriptionSetting("Skidded from catterpillow"));
        this.registerSetting(range = new SliderSetting("Range", 4, 1, 20, 1));
        this.registerSetting(cpsCap = new TickSetting("Cps cap", false));
        this.registerSetting(clientRots = new TickSetting("Rotate client-side", true));
    }

    @SubscribeEvent
    public void onTick(TickEvent e) {
        if (cpsCap.isToggled()) {
            this.placeBlock(range.getInput(), true);
        }
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent e) {
        if (!e.isPre()) return;
        if (clientRots.isToggled()) return;
        if(!Utils.Player.isPlayerInGame()) return;

        e.setYaw(serverYaw);
        e.setPitch(serverYaw);
    }

    @SubscribeEvent
    public void lookEvent(LookEvent e) {
        if (clientRots.isToggled()) return;
        e.setYaw(serverYaw);
        e.setPitch(serverPitch);
    }

    public void onEnable() {
        this.serverYaw = mc.thePlayer.rotationYaw;
        this.serverPitch = mc.thePlayer.rotationPitch;
    }
    
    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (!cpsCap.isToggled()) {
            this.placeBlock(range.getInput(), true, event.getPartialTicks());
        } else {
            this.placeBlock(range.getInput(), false, event.getPartialTicks());
        }
    }

    public void placeBlock(double range, boolean place, float partialTicks) {
        if (!CaterpillowUtils.isAirBlock(new BlockPos(mc.thePlayer).down())) {
            return;
        }
        if (this.placeBlockSimple(new BlockPos(mc.thePlayer).down(), place, partialTicks)) {
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

    public void placeBlock(double range, boolean place) {
        if (!CaterpillowUtils.isAirBlock(new BlockPos(mc.thePlayer).down())) {
            return;
        }
        if (this.placeBlockSimple(new BlockPos(mc.thePlayer).down(), place)) {
            return;
        }
        for (int dist = 0; dist <= range; ++dist) {
            for (int blockDist = 0; dist != blockDist; ++blockDist) {
                for (int x = blockDist; x >= 0; --x) {
                    int z = blockDist - x;
                    int y = dist - blockDist;
                    if (this.placeBlockSimple(new BlockPos(mc.thePlayer).down(y).north(x).west(z), place)) {
                        return;
                    }
                    if (this.placeBlockSimple(new BlockPos(mc.thePlayer).down(y).north(x).west(-z), place)) {
                        return;
                    }
                    if (this.placeBlockSimple(new BlockPos(mc.thePlayer).down(y).north(-x).west(z), place)) {
                        return;
                    }
                    if (!this.placeBlockSimple(new BlockPos(mc.thePlayer).down(y).north(-x).west(-z), place)) continue;
                    return;
                }
            }
        }
    }



    public void setRots(float yaw, float pitch) {
        if (clientRots.isToggled()) {
            mc.thePlayer.rotationYaw = yaw;
            mc.thePlayer.rotationPitch = pitch;
            mc.getRenderViewEntity().rotationYaw = yaw;
            mc.getRenderViewEntity().rotationPitch = pitch;
        }
        serverYaw = yaw;
        serverPitch = pitch;
    }

    public boolean placeBlockSimple(BlockPos pos, boolean place, float partialTicks) {
        if (!CaterpillowUtils.doesSlotHaveBlocks(mc.thePlayer.inventory.currentItem)) {
            mc.thePlayer.inventory.currentItem = CaterpillowUtils.getFirstHotBarSlotWithBlocks();
        }
        Minecraft mc = Minecraft.getMinecraft();
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
            if (place) {
                setRots(angles[0], angles[1]);
                mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), neighbor, side2, hitVec);
                mc.thePlayer.swingItem();
            }
            return true;
        }
        return false;
    }

    public boolean placeBlockSimple(BlockPos pos, boolean place) {
        Minecraft mc = Minecraft.getMinecraft();
        Entity entity = mc.getRenderViewEntity();
        double d0 = entity.posX;
        double d1 = entity.posY;
        double d2 = entity.posZ;
        Vec3 eyesPos = new Vec3(d0, d1 + (double)mc.thePlayer.getEyeHeight(), d2);
        for (EnumFacing side : EnumFacing.values()) {
            Vec3 hitVec;
            if (side.equals(EnumFacing.UP) || side.equals(EnumFacing.DOWN) && !Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) continue;
            BlockPos neighbor = pos.offset(side);
            EnumFacing side2 = side.getOpposite();
            if (!CaterpillowUtils.getBlock(neighbor).canCollideCheck(mc.theWorld.getBlockState(neighbor), false) || eyesPos.squareDistanceTo(hitVec = new Vec3(neighbor).addVector(0.5, 0.5, 0.5).add(new Vec3(side2.getDirectionVec()))) > 36.0) continue;
            float[] angles = CaterpillowUtils.getRotations(neighbor, side2);
            if (place) {
                setRots(angles[0], angles[1]);
                mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), neighbor, side2, hitVec);
                mc.thePlayer.swingItem();
            }
            return true;
        }
        return false;
    }


}
