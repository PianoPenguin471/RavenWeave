package ravenweave.client.module.modules.beta;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.weavemc.loader.api.event.RenderGameOverlayEvent;
import net.weavemc.loader.api.event.SubscribeEvent;
import net.weavemc.loader.api.event.TickEvent;
import org.lwjgl.input.Keyboard;
import ravenweave.client.event.impl.LookEvent;
import ravenweave.client.event.impl.UpdateEvent;
import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.DescriptionSetting;
import ravenweave.client.module.setting.impl.SliderSetting;
import ravenweave.client.module.setting.impl.TickSetting;
import ravenweave.client.utils.Utils;

public class AutoClutch extends Module {
    public static SliderSetting range;
    public static TickSetting cpsCap, clientRots;
    private float startYaw, startPitch, serverYaw, serverPitch;
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
        this.startYaw = mc.thePlayer.rotationYaw;
        this.startPitch = mc.thePlayer.rotationPitch;
        this.serverYaw = mc.thePlayer.rotationYaw;
        this.serverPitch = mc.thePlayer.rotationPitch;
    }

    public void onDisable() {
        mc.thePlayer.rotationYaw = startYaw;
        mc.thePlayer.rotationPitch = startPitch;
    }
    
    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (!cpsCap.isToggled()) {
            this.placeBlock(range.getInput(), true, event.getPartialTicks());
        } else {
            this.placeBlock(range.getInput(), false, event.getPartialTicks());
        }
    }

    public boolean placeBlock(double range, boolean place, float partialTicks) {
        if (!this.isAirBlock(getBlock(new BlockPos(mc.thePlayer).down()))) {
            return true;
        }
        if (this.placeBlockSimple(new BlockPos(mc.thePlayer).down(), place, partialTicks)) {
            return true;
        }
        Object target = null;
        for (int dist = 0; dist <= range; ++dist) {
            for (int blockDist = 0; dist != blockDist; ++blockDist) {
                for (int x = blockDist; x >= 0; --x) {
                    int z = blockDist - x;
                    int y = dist - blockDist;
                    if (this.placeBlockSimple(new BlockPos(mc.thePlayer).down(y).north(x).west(z), place, partialTicks)) {
                        return true;
                    }
                    if (this.placeBlockSimple(new BlockPos(mc.thePlayer).down(y).north(x).west(-z), place, partialTicks)) {
                        return true;
                    }
                    if (this.placeBlockSimple(new BlockPos(mc.thePlayer).down(y).north(-x).west(z), place, partialTicks)) {
                        return true;
                    }
                    if (!this.placeBlockSimple(new BlockPos(mc.thePlayer).down(y).north(-x).west(-z), place, partialTicks)) continue;
                    return true;
                }
            }
        }
        return false;
    }

    public void placeBlock(double range, boolean place) {
        if (!this.isAirBlock(getBlock(new BlockPos(mc.thePlayer).down()))) {
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


    public boolean isAirBlock(Block block) {
        if (block.getMaterial().isReplaceable()) {
            return !(block instanceof BlockSnow) || !(block.getBlockBoundsMaxY() > 0.125);
        }
        return false;
    }

    public int getFirstHotBarSlotWithBlocks() {
        for (int i = 0; i < 9; ++i) {
            if (mc.thePlayer.inventory.getStackInSlot(i) == null || !(mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemBlock)) continue;
            return i;
        }
        return 0;
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
        if (!this.doesSlotHaveBlocks(mc.thePlayer.inventory.currentItem)) {
            mc.thePlayer.inventory.currentItem = this.getFirstHotBarSlotWithBlocks();
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
            if (!getBlock(neighbor).canCollideCheck(mc.theWorld.getBlockState(neighbor), false) || eyesPos.squareDistanceTo(hitVec = new Vec3(neighbor).addVector(0.5, 0.5, 0.5).add(new Vec3(side2.getDirectionVec()))) > 36.0) continue;
            float[] angles = this.getRotations(neighbor, side2, partialTicks);
            if (place) {
                setRots(angles[0], angles[1]);
                mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), neighbor, side2, hitVec);
                mc.thePlayer.swingItem();
            }
            return true;
        }
        return false;
    }

    public boolean doesSlotHaveBlocks(int slotToCheck) {
        return mc.thePlayer.inventory.getStackInSlot(slotToCheck) != null && mc.thePlayer.inventory.getStackInSlot(slotToCheck).getItem() instanceof ItemBlock && mc.thePlayer.inventory.getStackInSlot((int)slotToCheck).stackSize > 0;
    }

    public boolean canPlace(BlockPos pos) {
        Minecraft mc = Minecraft.getMinecraft();
        Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbor = pos.offset(side);
            EnumFacing side2 = side.getOpposite();
            Vec3 hitVec = new Vec3(neighbor).addVector(0.5, 0.5, 0.5).add(new Vec3(side2.getDirectionVec()));
            if (eyesPos.squareDistanceTo(hitVec) > 36.0) continue;
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
            if (!getBlock(neighbor).canCollideCheck(mc.theWorld.getBlockState(neighbor), false) || eyesPos.squareDistanceTo(hitVec = new Vec3(neighbor).addVector(0.5, 0.5, 0.5).add(new Vec3(side2.getDirectionVec()))) > 36.0) continue;
            float[] angles = this.getRotations(neighbor, side2);
            if (place) {
                setRots(angles[0], angles[1]);
                mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), neighbor, side2, hitVec);
                mc.thePlayer.swingItem();
            }
            return true;
        }
        return false;
    }

    public static Block getBlock(BlockPos pos) {
        return Minecraft.getMinecraft().theWorld.getBlockState(pos).getBlock();
    }

    public float[] getRotations(BlockPos block, EnumFacing face, float partialTicks) {
        Entity entity = mc.getRenderViewEntity();
        double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
        double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
        double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
        double x = (double)block.getX() + 0.5 - posX + (double)face.getFrontOffsetX() / 2.0;
        double z = (double)block.getZ() + 0.5 - posZ + (double)face.getFrontOffsetZ() / 2.0;
        double y = (double)block.getY() + 0.5;
        double d1 = posY + (double)mc.thePlayer.getEyeHeight() - y;
        double d3 = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float)(Math.atan2(z, x) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(Math.atan2(d1, d3) * 180.0 / Math.PI);
        if (yaw < 0.0f) {
            yaw += 360.0f;
        }
        return new float[]{yaw, pitch};
    }

    public float[] getRotations(BlockPos block, EnumFacing face) {
        Entity entity = mc.getRenderViewEntity();
        double posX = entity.posX;
        double posY = entity.posY;
        double posZ = entity.posZ;
        double x = (double)block.getX() + 0.5 - posX + (double)face.getFrontOffsetX() / 2.0;
        double z = (double)block.getZ() + 0.5 - posZ + (double)face.getFrontOffsetZ() / 2.0;
        double y = (double)block.getY() + 0.5;
        double d1 = posY + (double)mc.thePlayer.getEyeHeight() - y;
        double d3 = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float)(Math.atan2(z, x) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(Math.atan2(d1, d3) * 180.0 / Math.PI);
        if (yaw < 0.0f) {
            yaw += 360.0f;
        }
        return new float[]{yaw, pitch};
    }
}
