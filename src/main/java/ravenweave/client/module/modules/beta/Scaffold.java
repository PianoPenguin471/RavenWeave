package ravenweave.client.module.modules.beta;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.weavemc.loader.api.event.RenderGameOverlayEvent;
import net.weavemc.loader.api.event.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import ravenweave.client.event.impl.LookEvent;
import ravenweave.client.event.impl.UpdateEvent;
import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.DescriptionSetting;
import ravenweave.client.module.setting.impl.SliderSetting;
import ravenweave.client.module.setting.impl.TickSetting;
import ravenweave.client.utils.Utils;

public class Scaffold extends Module {

    private final TickSetting disableSprint, noSwing;
    private final SliderSetting pitch;

    private float yaw, prevYaw;
    private BlockPos lastPos;


    public Scaffold() {
        super("Scaffold", ModuleCategory.beta); // Category: World
        this.registerSetting(new DescriptionSetting("Helps you make bridges/scaffold walk.")); // bad description, but manthe wrote it
        this.registerSetting(pitch = new SliderSetting("Pitch", 81, 70, 90, 1));
        this.registerSettings(noSwing = new TickSetting("No Swing", false));
        this.registerSetting(disableSprint = new TickSetting("Disable sprint", true));
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent e) {
        if(!Utils.Player.isPlayerInGame()) {
            return;
        }
        yaw = mc.thePlayer.rotationYaw - 180;
        float[] rots = new float[]{yaw, (float) pitch.getInput()};

        e.setYaw(rots[0]);
        if (mc.gameSettings.keyBindJump.isKeyDown()/* && !mc.gameSettings.keyBindForward.isKeyDown()*/) {
            e.setPitch(90);
        } else {
            e.setPitch(rots[1]);
        }
        mc.thePlayer.renderYawOffset = rots[0];
        mc.thePlayer.rotationYawHead = rots[0];

        prevYaw = e.getYaw();
    }

    @SubscribeEvent
    public void lookEvent(LookEvent e) {
        e.setPrevYaw(prevYaw);
        e.setYaw(yaw);

        if (mc.gameSettings.keyBindJump.isKeyDown()/* && !mc.gameSettings.keyBindForward.isKeyDown()*/) {
            e.setPitch(90);
            e.setPrevPitch(90);
        } else {
            e.setPrevPitch((float) pitch.getInput());
            e.setPitch((float) pitch.getInput());
        }
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Pre event) {
        MovingObjectPosition mop = mc.objectMouseOver;

        if (shouldClickBlock(mop)) {
            clickBlock(mop.getBlockPos(), mop.sideHit, mop.hitVec);
        }
        if (disableSprint.isToggled()) {
            mc.thePlayer.setSprinting(false);
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
            KeyBinding.onTick(mc.gameSettings.keyBindSprint.getKeyCode());
        }
    }

    public boolean shouldClickBlock(MovingObjectPosition mop) {
        if (!Utils.Player.isPlayerInGame()) return false;
        ItemStack heldItem = mc.thePlayer.getHeldItem();
        if (mc.currentScreen != null || heldItem == null || mop == null) return false;
        if (!(heldItem.getItem() instanceof ItemBlock)) return false;
        if (mop.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return false;
        BlockPos pos = mop.getBlockPos();
        if (mop.sideHit == EnumFacing.UP) {
            if (!Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) return false;
            // Remove excessive clicks
            if (!((ItemBlock) mc.thePlayer.getHeldItem().getItem()).canPlaceBlockOnSide(mc.theWorld, pos, mop.sideHit, mc.thePlayer, mc.thePlayer.getHeldItem())) return false;
        }



        if (mop.sideHit == EnumFacing.DOWN) return false;
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        if (block == null || block == Blocks.air || block instanceof BlockLiquid) return false;

        return true;
    }

    public void clickBlock(BlockPos pos, EnumFacing enumFacing, Vec3 vec3) {
        new Thread(() -> {
            try {
                boolean success = mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), pos, enumFacing, vec3);
                if (!noSwing.isToggled()) {
                    mc.thePlayer.swingItem();
                }
                if (success) {
                    lastPos = pos;
                }
            } catch (Exception ignored) {}
        }).start();
    }
}
