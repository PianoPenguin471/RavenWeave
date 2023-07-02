package ravenweave.client.module.modules.player;

import net.weavemc.loader.api.event.RenderGameOverlayEvent;
import net.weavemc.loader.api.event.SubscribeEvent;
import ravenweave.client.main.Raven;
import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.ComboSetting;
import ravenweave.client.module.setting.impl.DescriptionSetting;
import ravenweave.client.module.setting.impl.SliderSetting;
import ravenweave.client.module.setting.impl.TickSetting;
import ravenweave.client.utils.Utils;

public class BridgeAssist extends Module {
    private final TickSetting setLook;
    private final TickSetting onSneak;
    private final TickSetting workWithSafeWalk;
    private final SliderSetting waitFor;
    private final ComboSetting<Utils.Modes.BridgeMode> assistMode;
    private final SliderSetting assistRange;
    private boolean waitingForAim;
    private boolean gliding;
    private long startWaitTime;
    private final float[] godbridgePos = {75.6f, -315, -225, -135, -45, 0, 45, 135, 225, 315};
    private final float[] moonwalkPos = {79.6f, -340, -290, -250, -200, -160, -110, -70, -20, 0, 20, 70, 110, 160, 200, 250, 290, 340};
    private final float[] breezilyPos = {79.9f, -360, -270, -180, -90, 0, 90, 180, 270, 360};
    private final float[] normalPos = {78f, -315, -225, -135, -45, 0, 45, 135, 225, 315};
    private double speedYaw, speedPitch;
    private float waitingForYaw, waitingForPitch;


    public BridgeAssist() {
        super("Bridge Assist", ModuleCategory.player);
        this.registerSetting(new DescriptionSetting("Best with fastplace, not autoplace"));
        this.registerSetting(new DescriptionSetting("Auto aims for bridging"));
        this.registerSetting(waitFor = new SliderSetting("Wait time (ms)", 500, 0, 5000, 25));
        this.registerSetting(setLook = new TickSetting("Set look pos", true));
        this.registerSetting(onSneak = new TickSetting("Work only when sneaking", true));
        this.registerSetting(workWithSafeWalk= new TickSetting("Work with safewalk", false));
        this.registerSetting(assistRange = new SliderSetting("Assist range", 10.0D, 1.0D, 40.0D, 1.0D));
        this.registerSetting(assistMode = new ComboSetting<>("Mode:", Utils.Modes.BridgeMode.GODBRIDGE));
    }

    @Override
    public void onEnable() {
        this.waitingForAim = false;
        this.gliding = false;
        super.onEnable();
    }

    @SubscribeEvent
    public void onRenderTick(RenderGameOverlayEvent e) {
        if (!this.enabled) return;
        if (!Utils.Player.isPlayerInGame()) {
            return;
        }

        Module safeWalk = Raven.moduleManager.getModuleByClazz(SafeWalk.class);
        if (safeWalk != null && safeWalk.isEnabled()) {
            if (!workWithSafeWalk.isToggled()) {
                return;
            }
        }

        if (!(Utils.Player.playerOverAir() && mc.thePlayer.onGround)) {
            return;
        }

        if (onSneak.isToggled()) {
            if (!mc.thePlayer.isSneaking()) {
                return;
            }
        }


        if (gliding){
            float mcYaw = mc.thePlayer.rotationYaw;
            float mcPitch = mc.thePlayer.rotationPitch;

            float yaw = mcYaw - (mcYaw/360) * 360;
            float pitch = mcPitch - (mcPitch/360) * 360;

            double ilovebloat1 = yaw - speedYaw,
                    ilovebloat2 = yaw + speedYaw,
                    ilovebloat3 = pitch - speedPitch,
                    ilovebloat4 = pitch + speedPitch;

            if (ilovebloat1 < 0)
                ilovebloat1 *= -1;

            if (ilovebloat2 < 0)
                ilovebloat2 *= -1;

            if (ilovebloat3 < 0)
                ilovebloat3 *= -1;

            if (ilovebloat4 < 0)
                ilovebloat4 *= -1;

            if (this.speedYaw > ilovebloat1 || this.speedYaw > ilovebloat2)
                mc.thePlayer.rotationYaw = this.waitingForYaw;

            if (this.speedPitch > ilovebloat3 || this.speedPitch > ilovebloat4)
                mc.thePlayer.rotationPitch = this.waitingForPitch;

            if (mc.thePlayer.rotationYaw < this.waitingForYaw)
                mc.thePlayer.rotationYaw += this.speedYaw;

            if (mc.thePlayer.rotationYaw > this.waitingForYaw)
                mc.thePlayer.rotationYaw -= this.speedYaw;

            if (mc.thePlayer.rotationPitch > this.waitingForPitch)
                mc.thePlayer.rotationPitch -= this.speedPitch;

            if (mc.thePlayer.rotationYaw == this.waitingForYaw && mc.thePlayer.rotationPitch == this.waitingForPitch) {
                gliding = false;
                this.waitingForAim = false;
            }
            return;
        }

        if (!waitingForAim) {
            waitingForAim = true;
            startWaitTime = System.currentTimeMillis();
            return;
        }

        if (System.currentTimeMillis() - startWaitTime < waitFor.getInput())
            return;

        float mcYaw = mc.thePlayer.rotationYaw;
        float mcPitch = mc.thePlayer.rotationPitch;

        float yaw = mcYaw - ((int)mcYaw/360) * 360;
        float pitch = mcPitch  - ((int)mcPitch /360) * 360;

        float range = (float) assistRange.getInput();

        switch (assistMode.getMode()) {
            case GODBRIDGE:
                if (godbridgePos[0] >= (pitch - range) && godbridgePos[0] <= (pitch + range)) {
                    for (int k = 1; k < godbridgePos.length; k++) {
                        if (godbridgePos[k] >= (yaw - range) && godbridgePos[k] <= (yaw + range)) {
                            aimAt(godbridgePos[0], godbridgePos[k], mcYaw, mcPitch );
                            this.waitingForAim = false;
                            return;
                        }
                    }
                }


            case MOONWALK:
                if (moonwalkPos[0] >= (pitch - range) && moonwalkPos[0] <= (pitch + range)) {
                    for (int k = 1; k < moonwalkPos.length; k++) {
                        if (moonwalkPos[k] >= (yaw - range) && moonwalkPos[k] <= (yaw + range)) {
                            aimAt(moonwalkPos[0], moonwalkPos[k], mcYaw, mcPitch );
                            this.waitingForAim = false;
                            return;
                        }
                    }
                }

            case BREEZILY:
                if (breezilyPos[0] >= (pitch - range) && breezilyPos[0] <= (pitch + range)) {
                    for (int k = 1; k < breezilyPos.length; k++) {
                        if (breezilyPos[k] >= (yaw - range) && breezilyPos[k] <= (yaw + range)) {
                            aimAt(breezilyPos[0], breezilyPos[k], mcYaw, mcPitch );
                            this.waitingForAim = false;
                            return;
                        }
                    }
                }

            case NORMAL:
                if (normalPos[0] >= (pitch - range) && normalPos[0] <= (pitch + range)) {
                    for (int k = 1; k < normalPos.length; k++) {
                        if (normalPos[k] >= (yaw - range) && normalPos[k] <= (yaw + range)) {
                            aimAt(normalPos[0], normalPos[k], mcYaw, mcPitch );
                            this.waitingForAim = false;
                            return;
                        }
                    }
                }
        }
        this.waitingForAim = false;
    }

    public void aimAt(float pitch, float yaw, float fuckedYaw, float fuckedPitch){
       if(setLook.isToggled()) {
               mc.thePlayer.rotationPitch = pitch + ((int)fuckedPitch/360) * 360;
               mc.thePlayer.rotationYaw = yaw;
        }
    }
}