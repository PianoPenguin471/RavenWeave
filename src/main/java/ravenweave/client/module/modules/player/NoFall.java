package ravenweave.client.module.modules.player;

import net.weavemc.api.event.SubscribeEvent;
import ravenweave.client.event.TickEvent;
import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.ComboSetting;
import ravenweave.client.module.setting.impl.DescriptionSetting;
import ravenweave.client.utils.Utils;

public class NoFall extends Module {
    public static DescriptionSetting warning;
    public static ComboSetting mode;

    int ticks;
    double dist;
    boolean spoofing;

    public NoFall() {
        super("NoFall", ModuleCategory.player);

        this.registerSetting(warning = new DescriptionSetting("HypixelSpoof silent flags."));
        this.registerSetting(mode = new ComboSetting("Mode", NoFallMode.Spoof));
    }

    @SubscribeEvent
    public void onTick(TickEvent e) {
        if (!Utils.Player.isPlayerInGame()) return;
        switch ((NoFallMode) mode.getMode()) {
            case Spoof:
                if (mc.thePlayer.fallDistance > 2.5D) {
                    mc.thePlayer.onGround = true;
                }
                break;
            case HypixelSpoof:
                if (mc.thePlayer.onGround) {
                    ticks = 0;
                    dist = 0;
                    spoofing = false;
                } else {
                    if (mc.thePlayer.fallDistance > 2) {
                        if (spoofing) {
                            ticks++;
                            mc.thePlayer.onGround = true;

                            if (ticks >= 2) {
                                spoofing = false;
                                ticks = 0;
                                dist = mc.thePlayer.fallDistance;
                            }
                        } else {
                            if (mc.thePlayer.fallDistance - dist > 2) {
                                spoofing = true;
                            }
                        }
                    }
                }
                break;
            case Verus:
                if (mc.thePlayer.onGround) {
                    dist = 0;
                    spoofing = false;
                } else {
                    if (mc.thePlayer.fallDistance > 2) {
                        if (spoofing) {
                            mc.thePlayer.onGround = true;
                            mc.thePlayer.motionY = 0;
                            spoofing = false;
                            dist = mc.thePlayer.fallDistance;
                        } else {
                            if (mc.thePlayer.fallDistance - dist > 2) {
                                spoofing = true;
                            }
                        }
                    }
                }
                break;
        }
    }

}