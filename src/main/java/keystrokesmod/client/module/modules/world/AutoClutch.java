package keystrokesmod.client.module.modules.world;

import keystrokesmod.client.event.impl.TickEvent;
import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.setting.impl.TickSetting;
import keystrokesmod.client.utils.PlaceUtils;
import net.weavemc.loader.api.event.RenderGameOverlayEvent;
import net.weavemc.loader.api.event.SubscribeEvent;


public class AutoClutch extends Module {
    public static TickSetting AutoPlace;
    public static TickSetting CPScap;
    public AutoClutch() {
        super("Auto Clutch", ModuleCategory.world);
        this.registerSetting(AutoPlace = new TickSetting("Auto Place", true));
        this.registerSetting(CPScap = new TickSetting("CPS Cap", false));
    }

    @SubscribeEvent
    public void onForgeEvent(final RenderGameOverlayEvent.Post event) {
        PlaceUtils.placeBlock(4, !CPScap.isToggled(), event.getPartialTicks());
    }
    @SubscribeEvent
    public void onTick(TickEvent e) {
        if (CPScap.isToggled()) {
            PlaceUtils.placeBlock(4, true);
        }
    }

}

