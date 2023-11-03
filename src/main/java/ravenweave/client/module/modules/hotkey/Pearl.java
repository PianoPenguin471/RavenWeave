package ravenweave.client.module.modules.hotkey;

import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemStack;
import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.SliderSetting;
import ravenweave.client.module.setting.impl.TickSetting;
import ravenweave.client.utils.Utils;

public class Pearl extends Module {
    private final TickSetting preferSlot;
    private final SliderSetting hotbarSlotPreference;

    public Pearl() {
        super("Pearl", ModuleCategory.hotkey);

        this.registerSetting(preferSlot = new TickSetting("Prefer a slot", false));
        this.registerSetting(hotbarSlotPreference = new SliderSetting("Prefer which slot", 6, 1, 9, 1));
    }

    public static boolean checkSlot(int slot) {
        ItemStack itemInSlot = mc.thePlayer.inventory.getStackInSlot(slot);

        return itemInSlot != null && itemInSlot.getItem() instanceof ItemEnderPearl;
    }

    @Override
    public void onEnable() {
        if (!Utils.Player.isPlayerInGame()) {
            return;
        }

        if (preferSlot.isToggled()) {
            int preferedSlot = (int) hotbarSlotPreference.getInput() - 1;

            if (checkSlot(preferedSlot)) {
                mc.thePlayer.inventory.currentItem = preferedSlot;
                this.disable();
                return;
            }
        }

        for (int slot = 0; slot <= 8; slot++) {
            if (checkSlot(slot)) {
                mc.thePlayer.inventory.currentItem = slot;
                this.disable();
                return;
            }
        }
        this.disable();
    }
}
