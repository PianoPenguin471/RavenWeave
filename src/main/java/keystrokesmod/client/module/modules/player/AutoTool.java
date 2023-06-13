package keystrokesmod.client.module.modules.player;

import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import me.PianoPenguin471.events.ClickBlockEvent;
import net.weavemc.loader.api.event.SubscribeEvent;
import keystrokesmod.client.module.Module;




public class AutoTool extends Module {
    public AutoTool() {
        super("AutoTool", ModuleCategory.player);
    }

    @SubscribeEvent
    public void onClick(ClickBlockEvent event) {
        switchSlot(event.getClickedBlock());
    }

    public void switchSlot(BlockPos blockPos) {
        float bestSpeed = 1F;
        int bestSlot = -1;

        Block block = mc.theWorld.getBlockState(blockPos).getBlock();

        for (int i = 0; i < 9; i++) {
            ItemStack item = mc.thePlayer.inventory.mainInventory[i];

            if (item == null) {
                continue;
            }

            float speed = item.getStrVsBlock(block);

            if (speed > bestSpeed) {
                bestSpeed = speed;
                bestSlot = i;
            }
        }

        if (bestSlot != -1) {
            mc.thePlayer.inventory.currentItem = bestSlot;
        }
    }
}
