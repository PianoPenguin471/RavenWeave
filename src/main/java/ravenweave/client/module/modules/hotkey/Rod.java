package ravenweave.client.module.modules.hotkey;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.DoubleSliderSetting;
import ravenweave.client.module.setting.impl.SliderSetting;
import ravenweave.client.module.setting.impl.TickSetting;
import ravenweave.client.utils.Utils;

public class Rod extends Module {
    private final DoubleSliderSetting delay;
    private final TickSetting preferSlot;
    private final SliderSetting hotbarSlotPreference;

    public Rod() {
        super("Rod", ModuleCategory.hotkey);
        this.registerSetting(delay = new DoubleSliderSetting("Delay", 50, 75, 0, 150, 1));
        this.registerSetting(preferSlot = new TickSetting("Prefer a slot", false));
        this.registerSetting(hotbarSlotPreference = new SliderSetting("Prefer which slot", 9, 1, 9, 1));
    }

    @Override
    public void onEnable() {
        if (!Utils.Player.isPlayerInGame()) return;

        if (mc.thePlayer.getHeldItem().getItem() instanceof ItemFishingRod) {
            this.disable();
            return;
        }

        if (preferSlot.isToggled()) {
            int preferedSlot = (int) hotbarSlotPreference.getInput() - 1;

            ItemStack itemInSlot = mc.thePlayer.inventory.getStackInSlot(preferedSlot);
            if (itemInSlot != null && itemInSlot.getItem() instanceof ItemFishingRod) {
                mc.thePlayer.inventory.currentItem = preferedSlot;
                this.disable();
                return;
            }
        }

        for (int slot = 0; slot <= 8; slot++) {
            ItemStack itemInSlot = mc.thePlayer.inventory.getStackInSlot(slot);
            if (itemInSlot != null && itemInSlot.getItem() instanceof ItemFishingRod) {
                if (mc.thePlayer.inventory.currentItem != slot) {
                    mc.thePlayer.inventory.currentItem = slot;
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                    try {
                        Thread.sleep((long) Utils.Java.simpleRandom((int)delay.getInputMin(), (int)delay.getInputMax()));
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                } else {
                    return;
                }
                this.disable();
                return;
            }
        }

        this.disable();
    }
}
