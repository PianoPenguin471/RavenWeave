package keystrokesmod.client.utils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import static keystrokesmod.client.main.Raven.mc;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.Block;
import org.lwjgl.input.Keyboard;





public class PlaceUtils {

    public static boolean placeBlock(final int range, final boolean place) {
        if (!isAirBlock(getBlock(new BlockPos(mc.thePlayer).down()))) {
            return true;
        }
        if (placeBlockSimple(new BlockPos(mc.thePlayer).down(), place)) {
            return true;
        }

        int dist = 0;
        final Block target = null;

        while (dist <= range) {
            for (int blockDist = 0; dist != blockDist; ++blockDist) {
                for (int x = blockDist; x >= 0; --x) {
                    final int z = blockDist - x;
                    final int y = dist - blockDist;

                    if (placeBlockSimple(new BlockPos(mc.thePlayer).add(0, -y, 0).add(x, 0, z), place)) {
                        return true;
                    }
                    if (placeBlockSimple(new BlockPos(mc.thePlayer).add(0, -y, 0).add(x, 0, -z), place)) {
                        return true;
                    }
                    if (placeBlockSimple(new BlockPos(mc.thePlayer).add(0, -y, 0).add(-x, 0, z), place)) {
                        return true;
                    }
                    if (placeBlockSimple(new BlockPos(mc.thePlayer).add(0, -y, 0).add(-x, 0, -z), place)) {
                        return true;
                    }
                }
            }
            ++dist;
        }

        return false;
    }

    public static boolean placeBlock(final int range, final boolean place, final float partialTicks) {
        if (!isAirBlock(getBlock(new BlockPos(mc.thePlayer).down()))) {
            return true;
        }
        if (placeBlockSimple(new BlockPos(mc.thePlayer).down(), place, partialTicks)) {
            return true;
        }

        int dist = 0;
        final Block target = null;

        while (dist <= range) {
            for (int blockDist = 0; dist != blockDist; ++blockDist) {
                for (int x = blockDist; x >= 0; --x) {
                    final int z = blockDist - x;
                    final int y = dist - blockDist;

                    if (placeBlockSimple(new BlockPos(mc.thePlayer).add(0, -y, 0).add(x, 0, z), place, partialTicks)) {
                        return true;
                    }
                    if (placeBlockSimple(new BlockPos(mc.thePlayer).add(0, -y, 0).add(x, 0, -z), place, partialTicks)) {
                        return true;
                    }
                    if (placeBlockSimple(new BlockPos(mc.thePlayer).add(0, -y, 0).add(-x, 0, z), place, partialTicks)) {
                        return true;
                    }
                    if (placeBlockSimple(new BlockPos(mc.thePlayer).add(0, -y, 0).add(-x, 0, -z), place, partialTicks)) {
                        return true;
                    }
                }
            }
            ++dist;
        }

        return false;
    }


    public static boolean placeBlockSimple(final BlockPos pos, final boolean place) {
        final Minecraft mc = Minecraft.getMinecraft();
        final Entity entity = mc.getRenderViewEntity();
        final double x = entity.posX;
        final double y = entity.posY;
        final double z = entity.posZ;
        final Vec3 eyesPos = new Vec3(x, y + mc.thePlayer.getEyeHeight(), z);
        for (final EnumFacing side : EnumFacing.values()) {
            if (!side.equals(EnumFacing.UP)) {
                if (!side.equals(EnumFacing.DOWN) || Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
                    final BlockPos neighbor = pos.offset(side);
                    final EnumFacing oppositeSide = side.getOpposite();
                    if (mc.theWorld.getBlockState(neighbor).getBlock().canCollideCheck(mc.theWorld.getBlockState(neighbor), false)) {
                        final Vec3 hitVec = new Vec3(neighbor.getX() + 0.5, neighbor.getY() + 0.5, neighbor.getZ() + 0.5).add(new Vec3(oppositeSide.getDirectionVec()));
                        if (eyesPos.squareDistanceTo(hitVec) <= 36.0) {
                            final float[] angles = getRotations(neighbor, oppositeSide);
                            mc.getRenderViewEntity().rotationYaw = angles[0];
                            mc.getRenderViewEntity().rotationPitch = angles[1];
                            if (place) {
                                mc.thePlayer.rotationYaw = angles[0];
                                mc.thePlayer.rotationPitch = angles[1];
                                mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), neighbor, oppositeSide, hitVec);
                                mc.thePlayer.swingItem();
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }



    public static boolean placeBlockSimple(final BlockPos pos, final boolean place, final float partialTicks) {
        if (!doesSlotHaveBlocks(mc.thePlayer.inventory.currentItem)) {
            mc.thePlayer.inventory.currentItem = getFirstHotBarSlotWithBlocks();
        }
        final Minecraft mc = Minecraft.getMinecraft();
        final Entity entity = mc.getRenderViewEntity();
        final double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks;
        final double d2 = entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks;
        final double d3 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks;
        final Vec3 eyesPos = new Vec3(d0, d2 + mc.thePlayer.getEyeHeight(), d3);
        for (final EnumFacing side : EnumFacing.values()) {
            if (!side.equals(EnumFacing.UP)) {
                if (!side.equals(EnumFacing.DOWN) || Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
                    final BlockPos neighbor = pos.offset(side);
                    final EnumFacing oppositeSide = side.getOpposite();
                    if (mc.theWorld.getBlockState(neighbor).getBlock().canCollideCheck(mc.theWorld.getBlockState(neighbor), false)) {
                        final Vec3 hitVec = new Vec3(neighbor.getX() + 0.5, neighbor.getY() + 0.5, neighbor.getZ() + 0.5).add(new Vec3(oppositeSide.getDirectionVec()));
                        if (eyesPos.squareDistanceTo(hitVec) <= 36.0) {
                            final float[] angles = getRotations(neighbor, oppositeSide);
                            mc.getRenderViewEntity().rotationYaw = angles[0];
                            mc.getRenderViewEntity().rotationPitch = angles[1];
                            if (place) {
                                mc.thePlayer.rotationYaw = angles[0];
                                mc.thePlayer.rotationPitch = angles[1];
                                mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), neighbor, oppositeSide, hitVec);
                                mc.thePlayer.swingItem();
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }






    public static boolean isAirBlock(final Block block) {
        return block.getMaterial().isReplaceable() && (!(block instanceof BlockSnow) || block.getBlockBoundsMaxY() <= 0.125);
    }

    public static Block getBlock(final BlockPos pos) {
        return Minecraft.getMinecraft().theWorld.getBlockState(pos).getBlock();
    }

    public static boolean doesSlotHaveBlocks(final int slotToCheck) {
        ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(slotToCheck);
        return itemStack != null && itemStack.getItem() instanceof ItemBlock && itemStack.stackSize > 0;
    }

    public static int getFirstHotBarSlotWithBlocks() {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
            if (itemStack != null && itemStack.getItem() instanceof ItemBlock) {
                return i;
            }
        }
        return 0;
    }

    public static float[] getRotations(final BlockPos block, final EnumFacing face, final float partialTicks) {
        final Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
        final double posX = entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks;
        final double posY = entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks;
        final double posZ = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks;
        final double x = block.getX() + 0.5 - posX + face.getFrontOffsetX() / 2.0;
        final double z = block.getZ() + 0.5 - posZ + face.getFrontOffsetZ() / 2.0;
        final double y = block.getY() + 0.5;
        final double d1 = posY + Minecraft.getMinecraft().thePlayer.getEyeHeight() - y;
        final double d2 = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float)(Math.atan2(z, x) * 180.0 / Math.PI) - 90.0f;
        final float pitch = (float)(Math.atan2(d1, d2) * 180.0 / Math.PI);
        if (yaw < 0.0f) {
            yaw += 360.0f;
        }
        return new float[] { yaw, pitch };
    }
    public static float[] getRotations(final BlockPos block, final EnumFacing face) {
        final Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
        final double posX = entity.posX;
        final double posY = entity.posY;
        final double posZ = entity.posZ;
        final double x = block.getX() + 0.5 - posX + face.getFrontOffsetX() / 2.0;
        final double z = block.getZ() + 0.5 - posZ + face.getFrontOffsetZ() / 2.0;
        final double y = block.getY() + 0.5;
        final double d1 = posY + Minecraft.getMinecraft().thePlayer.getEyeHeight() - y;
        final double d2 = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float) (Math.atan2(z, x) * 180.0 / Math.PI) - 90.0f;
        final float pitch = (float) (Math.atan2(d1, d2) * 180.0 / Math.PI);
        if (yaw < 0.0f) {
            yaw += 360.0f;
        }
        return new float[]{yaw, pitch};
    }

}
