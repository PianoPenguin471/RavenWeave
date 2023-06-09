package ravenweave.client.module.modules.beta;

import net.minecraft.client.settings.KeyBinding;
import net.weavemc.loader.api.event.RenderGameOverlayEvent;
import net.weavemc.loader.api.event.SubscribeEvent;
import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.SliderSetting;
import ravenweave.client.utils.CoolDown;
import ravenweave.client.utils.Utils;

public class SpeedTest extends Module {

    private CoolDown coolDown = new CoolDown(1);
    private SliderSetting delay, stopPercent;
    
    public SpeedTest() {
        super("SpeedTest", ModuleCategory.beta); // Category: Movement
        this.registerSetting(delay = new SliderSetting("Delay", 20, 0, 300 ,1));
        this.registerSetting(stopPercent = new SliderSetting("Stop Percent", 0, 0, 200 ,1));
    }
    
    @SubscribeEvent
    public void onRender2D(RenderGameOverlayEvent e) {
        if (!this.enabled) return;
        if(!Utils.Player.isPlayerInGame())
            return;
        if(mc.thePlayer.onGround && coolDown.hasFinished()) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), true);
            coolDown.setCooldown((long)delay.getInput());
            coolDown.start();
        }
        if(coolDown.firstFinish()) {
            mc.thePlayer.motionY *= stopPercent.getInput()/100f;
        }
    }
    
    @Override
    public void onDisable() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
    }
}
