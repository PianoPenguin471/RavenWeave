package ravenweave.client.module.modules.combat;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;
import net.weavemc.loader.api.event.SubscribeEvent;
import ravenweave.client.event.UpdateEvent;
import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.DoubleSliderSetting;
import ravenweave.client.module.setting.impl.SliderSetting;
import ravenweave.client.module.setting.impl.TickSetting;
import ravenweave.client.utils.CoolDown;
import ravenweave.client.utils.Utils;

public class AutoSoup extends Module {
    private final DoubleSliderSetting delay, coolDown;
    private final SliderSetting health;
    private final CoolDown cd = new CoolDown(1);
    private final TickSetting invConsume;
    private State state = State.WAITINGTOSWITCH;
    private int originalSlot;

    public AutoSoup() {
        super("AutoSoup", ModuleCategory.combat);
        this.registerSetting(health = new SliderSetting("Health", 7, 0, 20, 0.1));
        this.registerSetting(delay = new DoubleSliderSetting("Delay", 50, 100, 0, 200, 1));
        this.registerSetting(coolDown = new DoubleSliderSetting("Cooldown", 1000, 1200, 0, 5000, 1));
        this.registerSetting(invConsume = new TickSetting("Consume in inventory", false));
    }

    @SubscribeEvent
    public void update(UpdateEvent e) {
        if(!Utils.Player.isPlayerInGame())
            return;
        if(
        		(invConsume.isToggled() || (mc.currentScreen == null))
        		&& (mc.thePlayer.getHealth() < health.getInput()) && cd.hasFinished()
        		) {
            switch(state) {
                case WAITINGTOSWITCH:
                    cd.setCooldown((int) Utils.Client.ranModuleVal(delay, Utils.Java.rand())/4);
                    break;
                case NONE:
                    int slot = getSoupSlot();
                    if(slot == -1 ) return;
                    originalSlot = mc.thePlayer.inventory.currentItem;
                    mc.thePlayer.inventory.currentItem = slot;

                    cd.setCooldown((int) Utils.Client.ranModuleVal(delay, Utils.Java.rand())/4);
                    break;
                case SWITCHED:
                    KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());

                    cd.setCooldown((int) Utils.Client.ranModuleVal(delay, Utils.Java.rand())/4);
                    break;
                case SWITCHEDANDCLICKED:
                    KeyBinding.onTick(mc.gameSettings.keyBindDrop.getKeyCode());

                    cd.setCooldown((int) Utils.Client.ranModuleVal(delay, Utils.Java.rand())/4);
                    break;
                case SWITCHEDANDDROPPED:
                    mc.thePlayer.inventory.currentItem = originalSlot;

                    //cd.setCooldown(1);
                    cd.setCooldown((int) Utils.Client.ranModuleVal(coolDown, Utils.Java.rand()));
                    break;
            }
            state = state.next();
            cd.start();
        }

    }

    public int getSoupSlot() {
        for (int slot = 0; slot <= 8; slot++) {
            ItemStack itemInSlot = mc.thePlayer.inventory.getStackInSlot(slot);
            if ((itemInSlot != null) && (itemInSlot.getItem() instanceof ItemSoup))
                return slot;
        }
        return -1;
    }

    public enum State {
    	WAITINGTOSWITCH, NONE, SWITCHED, SWITCHEDANDCLICKED, SWITCHEDANDDROPPED;

        private static final State[] vals = values();
        public State next() {
            return vals[(this.ordinal()+1) % vals.length];
        }
    }

}
