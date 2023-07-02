package ravenweave.client.module.modules.beta;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.gui.ScaledResolution;
import net.weavemc.loader.api.event.RenderGameOverlayEvent;
import net.weavemc.loader.api.event.SubscribeEvent;
import ravenweave.client.event.impl.GameLoopEvent;
import ravenweave.client.event.impl.LookEvent;
import ravenweave.client.event.impl.MoveInputEvent;
import ravenweave.client.event.impl.UpdateEvent;
import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.DescriptionSetting;
import ravenweave.client.module.setting.impl.SliderSetting;
import ravenweave.client.module.setting.impl.TickSetting;
import ravenweave.client.utils.font.FontUtil;

public class Scaffold extends Module {

    private TickSetting eagle;
    private SliderSetting rps;
    private DescriptionSetting description;

    private float yaw, pitch, locked;
    private int blockCount;


    public Scaffold() {
        super("Scaffold", ModuleCategory.beta); // Category: World
        this.registerSetting(description = new DescriptionSetting("Bridges for you"));
        this.registerSettings(eagle = new TickSetting("Shift", false));
        this.registerSettings(rps = new SliderSetting("Rotation speed", 80, 0, 300, 1));
    }

    @Subscribe
    public void gameLoopEvent(GameLoopEvent e) {
        updateBlockCount();

    }

    @Subscribe
    public void moveEvent(MoveInputEvent e) {

    }

    @Subscribe
    public void updateEvent(UpdateEvent e) {
        e.setPitch(pitch);
        e.setYaw(yaw);
    }

    @Subscribe
    public void lookEvent(LookEvent e) {
        e.setPitch(pitch);
        e.setYaw(yaw);
    }

    @SubscribeEvent
    public void onRender2D(RenderGameOverlayEvent e) {
        if (!this.enabled) return;
        ScaledResolution sr = new ScaledResolution(mc);
        FontUtil.normal.drawCenteredSmoothString(blockCount + " blocks", (int) (sr.getScaledWidth()/2f+8), (int) (sr.getScaledHeight()/2f-4), blockCount <= 16? 0xff0000 : -1);
    }

    private void updateBlockCount() {
        blockCount = 3;
        /*
        for (int i = 0; i < InventoryUtils.END; i++) {
            final ItemStack stack = InventoryUtils.getStackInSlot(i);

            if (stack != null && stack.getItem() instanceof ItemBlock &&
                    InventoryUtils.isGoodBlockStack(stack))
                blockCount += stack.stackSize;
        }*/

    }


}
