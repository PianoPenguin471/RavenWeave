package ravenweave.client.module.modules.player;

import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.weavemc.loader.api.event.SubscribeEvent;
import ravenweave.client.event.PacketEvent;
import ravenweave.client.event.UpdateEvent;
import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.DoubleSliderSetting;
import ravenweave.client.module.setting.impl.SliderSetting;
import ravenweave.client.module.setting.impl.TickSetting;
import ravenweave.client.utils.ItemUtils;
import ravenweave.client.utils.TimerUtil;
import ravenweave.client.utils.Utils;

public class Manager extends Module {
    public static DoubleSliderSetting delay;
    public static TickSetting legit, keepTools;
    public static SliderSetting swordSlot, blockSlot;
    public static int pickaxeSlotInt, axeSlotInt, shovelSlotInt, potionSlotInt, foodSlotInt;

    public Manager() {
        super("Manager", ModuleCategory.player);
        this.registerSetting(legit = new TickSetting("Legit", true));
        this.registerSetting(delay = new DoubleSliderSetting("Delay", 100, 150, 50, 500, 50));
        this.registerSetting(swordSlot = new SliderSetting("Sword Slot", 1, 1, 9, 1));
        this.registerSetting(blockSlot = new SliderSetting("Blocks Slot", 3, 1, 9, 1));
        this.registerSetting(keepTools = new TickSetting("Keep Tools", true));
    }

    private final TimerUtil stopwatch = new TimerUtil();
    private int chestTicks, attackTicks, placeTicks;
    private boolean moved, open;
    private long nextClick;

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (!event.isPre()) return;
        if (mc.thePlayer.ticksExisted <= 40) return;

        if (mc.currentScreen instanceof GuiChest) {
            this.chestTicks = 0;
        } else {
            this.chestTicks++;
        }

        this.attackTicks++;
        this.placeTicks++;

        if (legit.isToggled() && !(mc.currentScreen instanceof GuiInventory)) {
            this.stopwatch.reset();
            return;
        }

        if (this.stopwatch.hasReached(this.nextClick) || this.chestTicks < 10 || this.attackTicks < 10 || this.placeTicks < 10) {
            this.closeInventory();
            return;
        }

        this.moved = false;

        int helmet = -1;
        int chestplate = -1;
        int leggings = -1;
        int boots = -1;

        int sword = -1;
        int pickaxe = -1;
        int axe = -1;
        int shovel = -1;
        int block = -1;
        int potion = -1;
        int food = -1;

        for (int i = 0; i < 40; i++) {
            final ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);

            if (stack == null) {
                continue;
            }

            final Item item = stack.getItem();

            if (!ItemUtils.useful(stack)) {
                this.throwItem(i);
            }

            if (item instanceof ItemArmor armor) {
                final int reduction = this.armorReduction(stack);

                switch (armor.armorType) {
                    case 0 -> {
                        if (helmet == -1 || reduction > armorReduction(mc.thePlayer.inventory.getStackInSlot(helmet))) {
                            helmet = i;
                        }
                    }
                    case 1 -> {
                        if (chestplate == -1 || reduction > armorReduction(mc.thePlayer.inventory.getStackInSlot(chestplate))) {
                            chestplate = i;
                        }
                    }
                    case 2 -> {
                        if (leggings == -1 || reduction > armorReduction(mc.thePlayer.inventory.getStackInSlot(leggings))) {
                            leggings = i;
                        }
                    }
                    case 3 -> {
                        if (boots == -1 || reduction > armorReduction(mc.thePlayer.inventory.getStackInSlot(boots))) {
                            boots = i;
                        }
                    }
                }
            }

            if (item instanceof ItemSword) {
                if (sword == -1 || damage(stack) > damage(mc.thePlayer.inventory.getStackInSlot(sword))) {
                    sword = i;
                }

                if (i != sword) {
                    this.throwItem(i);
                }
            }

            if (item instanceof ItemPickaxe) {
                if (pickaxe == -1 || mineSpeed(stack) > mineSpeed(mc.thePlayer.inventory.getStackInSlot(pickaxe))) {
                    pickaxe = i;
                }

                if (i != pickaxe) {
                    this.throwItem(i);
                }
            }

            if (item instanceof ItemAxe) {
                if (axe == -1 || mineSpeed(stack) > mineSpeed(mc.thePlayer.inventory.getStackInSlot(axe))) {
                    axe = i;
                }

                if (i != axe) {
                    this.throwItem(i);
                }
            }

            if (item instanceof ItemSpade) {
                if (shovel == -1 || mineSpeed(stack) > mineSpeed(mc.thePlayer.inventory.getStackInSlot(shovel))) {
                    shovel = i;
                }

                if (i != shovel) {
                    this.throwItem(i);
                }
            }

            if (item instanceof ItemBlock) {
                if (block == -1) {
                    block = i;
                } else {
                    final ItemStack currentStack = mc.thePlayer.inventory.getStackInSlot(block);

                    if (currentStack != null && stack.stackSize > currentStack.stackSize) {
                        block = i;
                    }
                }
            }

            if (item instanceof ItemPotion) {
                if (potion == -1) {
                    potion = i;
                } else {
                    final ItemStack currentStack = mc.thePlayer.inventory.getStackInSlot(potion);

                    if (currentStack == null) {
                        continue;
                    }

                    final ItemPotion currentItemPotion = (ItemPotion) currentStack.getItem();
                    final ItemPotion itemPotion = (ItemPotion) item;

                    boolean foundCurrent = false;

                    for (final PotionEffect e : mc.thePlayer.getActivePotionEffects()) {
                        if (e.getPotionID() == currentItemPotion.getEffects(currentStack).get(0).getPotionID() && e.getDuration() > 0) {
                            foundCurrent = true;
                            break;
                        }
                    }

                    boolean found = false;

                    for (final PotionEffect e : mc.thePlayer.getActivePotionEffects()) {
                        if (e.getPotionID() == itemPotion.getEffects(stack).get(0).getPotionID() && e.getDuration() > 0) {
                            found = true;
                            break;
                        }
                    }

                    if (itemPotion.getEffects(stack) != null && currentItemPotion.getEffects(currentStack) != null) {
                        if ((Utils.Player.potionRanking(itemPotion.getEffects(stack).get(0).getPotionID()) > Utils.Player.potionRanking(currentItemPotion.getEffects(currentStack).get(0).getPotionID()) || foundCurrent) && !found) {
                            potion = i;
                        }
                    }
                }
            }

            if (item instanceof ItemFood) {
                if (food == -1) {
                    food = i;
                } else {
                    final ItemStack currentStack = mc.thePlayer.inventory.getStackInSlot(food);

                    if (currentStack == null) {
                        continue;
                    }

                    final ItemFood currentItemFood = (ItemFood) currentStack.getItem();
                    final ItemFood itemFood = (ItemFood) item;

                    if (itemFood.getSaturationModifier(stack) > currentItemFood.getSaturationModifier(currentStack)) {
                        food = i;
                    }
                }
            }
        }

        for (int i = 0; i < 40; i++) {
            final ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);

            if (stack == null) {
                continue;
            }

            final Item item = stack.getItem();

            if (item instanceof ItemArmor armor) {

                switch (armor.armorType) {
                    case 0 -> {
                        if (i != helmet) {
                            this.throwItem(i);
                        }
                    }
                    case 1 -> {
                        if (i != chestplate) {
                            this.throwItem(i);
                        }
                    }
                    case 2 -> {
                        if (i != leggings) {
                            this.throwItem(i);
                        }
                    }
                    case 3 -> {
                        if (i != boots) {
                            this.throwItem(i);
                        }
                    }
                }
            }
        }

        if (helmet != -1 && helmet != 39) {
            this.equipItem(helmet);
        }

        if (chestplate != -1 && chestplate != 38) {
            this.equipItem(chestplate);
        }

        if (leggings != -1 && leggings != 37) {
            this.equipItem(leggings);
        }

        if (boots != -1 && boots != 36) {
            this.equipItem(boots);
        }

        getSlots((int)swordSlot.getInput(), (int)blockSlot.getInput());

        if (sword != -1 && sword != (int) swordSlot.getInput() - 1) {
            this.moveItem(sword, (int) (swordSlot.getInput() - 37));
        }

        if (block != -1 && block != (int) blockSlot.getInput() - 1) {
            this.moveItem(block, (int) (blockSlot.getInput() - 37));
        }

        if (pickaxe != -1 && pickaxe != pickaxeSlotInt - 1) {
            if (!keepTools.isToggled()) {
                throwItem(pickaxeSlotInt);
            } else {
                this.moveItem(pickaxe, pickaxeSlotInt - 37);
            }
        }

        if (axe != -1 && axe != axeSlotInt - 1) {
            if (!keepTools.isToggled()) {
                throwItem(axeSlotInt);
            } else {
                this.moveItem(pickaxe, axeSlotInt - 37);
            }
        }

        if (shovel != -1 && shovel != shovelSlotInt - 1) {
            if (!keepTools.isToggled()) {
                throwItem(shovelSlotInt);
            } else {
                this.moveItem(pickaxe, shovelSlotInt - 37);
            }
        }

        if (potion != -1 && potion != potionSlotInt - 1) {
            this.moveItem(potion, potionSlotInt - 37);
        }

        if (food != -1 && food != foodSlotInt - 1) {
            this.moveItem(food, foodSlotInt - 37);
        }

        if (this.canOpenInventory() && !this.moved) {
            this.closeInventory();
        }
    };

    @Override
    public void onDisable() {
        if (this.canOpenInventory()) {
            this.closeInventory();
        }
    }

    private void openInventory() {
        if (!this.open) {
            mc.getNetHandler().addToSendQueue((new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT)));
            this.open = true;
        }
    }

    private void closeInventory() {
        if (this.open) {
            mc.getNetHandler().addToSendQueue((new C0DPacketCloseWindow(mc.thePlayer.inventoryContainer.windowId)));
            this.open = false;
        }
    }

    private boolean canOpenInventory() {
        return !(mc.currentScreen instanceof GuiInventory);
    }

    private void throwItem(final int slot) {
        if ((!this.moved || this.nextClick <= 0)) {

            if (this.canOpenInventory()) {
                this.openInventory();
            }

            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, this.slot(slot), 1, 4, mc.thePlayer);

            this.nextClick = Math.round(Utils.Java.simpleRandom((int) delay.getInputMin(), (int) delay.getInputMax()));
            this.stopwatch.reset();
            this.moved = true;
        }
    }

    private void moveItem(final int slot, final int destination) {
        if ((!this.moved || this.nextClick <= 0)) {

            if (this.canOpenInventory()) {
                this.openInventory();
            }

            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, this.slot(slot), this.slot(destination), 2, mc.thePlayer);

            this.nextClick = Math.round(Utils.Java.simpleRandom((int) delay.getInputMin(), (int) delay.getInputMax()));
            this.stopwatch.reset();
            this.moved = true;
        }
    }

    private void equipItem(final int slot) {
        if ((!this.moved || this.nextClick <= 0)) {

            if (this.canOpenInventory()) {
                this.openInventory();
            }

            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, this.slot(slot), 0, 1, mc.thePlayer);

            this.nextClick = Math.round(Utils.Java.simpleRandom((int) delay.getInputMin(), (int) delay.getInputMax()));
            this.stopwatch.reset();
            this.moved = true;
        }
    }

    private float damage(final ItemStack stack) {
        final ItemSword sword = (ItemSword) stack.getItem();
        final int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack);
        return (float) (sword.getDamageVsEntity() + level * 1.25);
    }

    private float mineSpeed(final ItemStack stack) {
        final Item item = stack.getItem();
        int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack);

        level = switch (level) {
            case 1 -> 30;
            case 2 -> 69;
            case 3 -> 120;
            case 4 -> 186;
            case 5 -> 271;
            default -> 0;
        };

        if (item instanceof ItemPickaxe pickaxe) {
            return pickaxe.getToolMaterial().getEfficiencyOnProperMaterial() + level;
        } else if (item instanceof ItemSpade shovel) {
            return shovel.getToolMaterial().getEfficiencyOnProperMaterial() + level;
        } else if (item instanceof ItemAxe axe) {
            return axe.getToolMaterial().getEfficiencyOnProperMaterial() + level;
        }

        return 0;
    }

    private int armorReduction(final ItemStack stack) {
        final ItemArmor armor = (ItemArmor) stack.getItem();
        return armor.damageReduceAmount + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{stack}, DamageSource.generic);
    }

    private int slot(final int slot) {
        if (slot >= 36) {
            return 8 - (slot - 36);
        }

        if (slot < 9) {
            return slot + 36;
        }

        return slot;
    }

    public static void getSlots(int int1, int int2) {
        int[] inventorySlots = new int[5];

        for (int i = 1; i <= 9; i++) {
            if (i != int1 && i != int2) {
                inventorySlots[0] = i;
                inventorySlots[1] = i;
                inventorySlots[2] = i;
                inventorySlots[3] = i;
                inventorySlots[4] = i;
            }
        }

        pickaxeSlotInt = inventorySlots[0];
        axeSlotInt = inventorySlots[1];
        shovelSlotInt = inventorySlots[2];
        potionSlotInt = inventorySlots[3];
        foodSlotInt = inventorySlots[4];
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (event.isOutgoing()) {
            if (event.getPacket() instanceof C08PacketPlayerBlockPlacement) {
                this.placeTicks = 0;
            }
        }
    }
}
