package keystrokesmod.client.module.modules.movement;

import club.maxstats.weave.loader.api.event.RenderGameOverlayEvent;
import club.maxstats.weave.loader.api.event.SubscribeEvent;

import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.setting.impl.SliderSetting;
import keystrokesmod.client.utils.CoolDown;
import keystrokesmod.client.utils.Utils;
import net.minecraft.client.settings.KeyBinding;

public class SpeedTest extends Module {

    private CoolDown coolDown = new CoolDown(1);
    private SliderSetting delay, stopPercent;
    
    public SpeedTest() {
        super("SpeedTest", /*ModuleCategory.movement*/ModuleCategory.beta);
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
