package ravenweave.client.module.modules.beta;

import com.google.common.eventbus.Subscribe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.settings.KeyBinding;
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
import org.lwjgl.input.Mouse;
import ravenweave.client.event.impl.GameLoopEvent;
import ravenweave.client.event.impl.LookEvent;
import ravenweave.client.event.impl.UpdateEvent;
import ravenweave.client.main.Raven;
import ravenweave.client.module.Module;
import ravenweave.client.module.modules.movement.Sprint;
import ravenweave.client.module.setting.impl.SliderSetting;
import ravenweave.client.module.setting.impl.TickSetting;
import ravenweave.client.utils.Utils;

import java.awt.*;

public class Scaffold extends Module {

    private final TickSetting disableSprint;
    private SliderSetting pitch;

    private float yaw, prevYaw;
    private int blockCount;
    private BlockPos lastPos;


    public Scaffold() {
        super("Scaffold", ModuleCategory.beta); // Category: World
        this.registerSetting(pitch = new SliderSetting("Pitch", 83, 70, 90, 1));
        this.registerSetting(disableSprint = new TickSetting("Disable sprint", true));
    }

    @Override
    public void onEnable() {
        if (disableSprint.isToggled()) {
            Raven.moduleManager.getModuleByClazz(Sprint.class).disable();
            try {
                Robot robot = new Robot();
                robot.keyPress(mc.gameSettings.keyBindSprint.getKeyCode());
                robot.keyRelease(mc.gameSettings.keyBindSprint.getKeyCode());
            } catch (AWTException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Subscribe
    public void onUpdate(UpdateEvent e) {
        if(!Utils.Player.isPlayerInGame()) {
            return;
        }
        yaw = mc.thePlayer.rotationYaw - 180;
        float[] rots = new float[]{yaw, (float) pitch.getInput()};

        e.setYaw(rots[0]);
        e.setPitch(rots[1]);

        //Third Person Rots
        mc.thePlayer.renderYawOffset = rots[0];
        mc.thePlayer.rotationYawHead = rots[0];

        //for GCD
        prevYaw = e.getYaw();
    }

    @Subscribe
    public void lookEvent(LookEvent e) {
        e.setPrevYaw(prevYaw);
        e.setPrevPitch((float) pitch.getInput());
        e.setYaw(yaw);
        e.setPitch((float) pitch.getInput());
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Pre event) {
        MovingObjectPosition mop = mc.objectMouseOver;

        if (shouldClickBlock(mop, mop.getBlockPos()))
            clickBlock(mop.getBlockPos(), mop.sideHit, mop.hitVec);
    }

    public boolean shouldClickBlock(MovingObjectPosition mop, BlockPos pos) {
        if (!Utils.Player.isPlayerInGame()) return false;
        // Check that we're holding a block
        ItemStack heldItem = mc.thePlayer.getHeldItem();

        // Null checks
        if (mc.currentScreen != null || heldItem == null || mop == null) return false;
        if (!(heldItem.getItem() instanceof ItemBlock)) return false;

        // Make sure we're looking at a block
        if (mop.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return false;

        // Make sure we're looking at the correct side of the block
        if (mop.sideHit == EnumFacing.UP && !Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) return false;

        // Prevent sus downstacks and excessive packets
        if (mop.sideHit == EnumFacing.DOWN) return false;

        Block block = mc.theWorld.getBlockState(pos).getBlock();

        // Make sure it's a valid block
        if (block == null || block == Blocks.air || block instanceof BlockLiquid) return false;

        return true;
    }

    public void clickBlock(BlockPos pos, EnumFacing enumFacing, Vec3 vec3) {
        new Thread(() -> {
            try {
                boolean success = mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), pos, enumFacing, vec3);
                mc.thePlayer.swingItem();

                if (success) {
                    lastPos = pos;
                }
            } catch (Exception ignored) {}
        }).start();
    }
}
