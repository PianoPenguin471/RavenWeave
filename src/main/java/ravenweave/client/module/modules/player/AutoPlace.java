package ravenweave.client.module.modules.player;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.weavemc.loader.api.event.RenderGameOverlayEvent;
import net.weavemc.loader.api.event.SubscribeEvent;
import org.lwjgl.input.Mouse;
import ravenweave.client.event.impl.TickEvent;
import ravenweave.client.main.Raven;
import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.SliderSetting;
import ravenweave.client.module.setting.impl.TickSetting;
import ravenweave.client.utils.Utils;

import java.util.Random;

public class AutoPlace extends Module {
    public Random random = new Random();
    public static TickSetting holdRight, b, placeOnTops;
    public static SliderSetting delay;
    private long lastPlace;
    private BlockPos lastPos;

    public AutoPlace() {
        super("AutoPlace", ModuleCategory.player);
        this.registerSetting(delay = new SliderSetting("Delay", 35.0D, 0.0D, 200.0D, 1.0D));
        this.registerSetting(holdRight = new TickSetting("Hold right", true));
        this.registerSetting(placeOnTops = new TickSetting("Place on top", false));
    }

    public void onDisable() {
        if (holdRight.isToggled()) {
            this.setRightClickDelay(4);
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent e) {
        Module fastPlace = Raven.moduleManager.getModuleByClazz(FastPlace.class);
        if (holdRight.isToggled() && Mouse.isButtonDown(1) && !mc.thePlayer.capabilities.isFlying && fastPlace != null
                && !fastPlace.isEnabled()) {
            ItemStack item = mc.thePlayer.getHeldItem();
            if (item == null || !(item.getItem() instanceof ItemBlock)) {
                return;
            }

            this.setRightClickDelay(mc.thePlayer.motionY > 0.0D ? 1 : 1000);
        }

    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Pre event) {
        // Check that we're not in a menu
        if (!Utils.Player.isPlayerInGame()) return;
        if (mc.currentScreen != null) return;

        // Check that we're holding a block
        ItemStack heldItem = mc.thePlayer.getHeldItem();
        if (heldItem == null) return;
        if (!(heldItem.getItem() instanceof ItemBlock)) return;


        MovingObjectPosition movingObjectPosition = mc.objectMouseOver;

        // Make sure we're looking at a block
        if (movingObjectPosition == null || movingObjectPosition.typeOfHit != MovingObjectType.BLOCK) return;

        // Make sure we're looking at the correct side of the block
        if (movingObjectPosition.sideHit == EnumFacing.UP && !placeOnTops.isToggled())
            return;
        mc.thePlayer.addChatMessage(new ChatComponentText(movingObjectPosition.toString()));
        BlockPos pos = movingObjectPosition.getBlockPos();

        Block block = mc.theWorld.getBlockState(pos).getBlock();

        // Make sure it's a valid block
        if (block == null || block == Blocks.air || block instanceof BlockLiquid) return;

        if (holdRight.isToggled() && !Mouse.isButtonDown(1)) return;

        if (lastPos == null || lastPos.equals(pos)) return;

        lastPos = pos;

        long now = System.currentTimeMillis();
        if (now - this.lastPlace >= delay.getInput()) {
            this.lastPlace = now;

            clickBlock();
        }
    }

    public void clickBlock() {
        new Thread(() -> {
            try {
                int keyCode = mc.gameSettings.keyBindUseItem.getKeyCode();
                KeyBinding.setKeyBindState(keyCode, true);
                mc.thePlayer.swingItem();
                KeyBinding.onTick(keyCode);

                Thread.sleep(2);

                mc.getItemRenderer().resetEquippedProgress();
                KeyBinding.setKeyBindState(keyCode, false);
            } catch (Exception ignored) {}
        }).start();
    }

    private void setRightClickDelay(int i) {
        try {
            if (FastPlace.rightClickDelayTimerField != null) {
                FastPlace.rightClickDelayTimerField.set(mc, i);
            }
        } catch (IllegalAccessException | IndexOutOfBoundsException ignored) {
        }
    }
}
