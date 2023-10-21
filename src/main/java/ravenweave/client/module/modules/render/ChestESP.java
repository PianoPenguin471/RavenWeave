package ravenweave.client.module.modules.render;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.weavemc.loader.api.event.RenderWorldEvent;
import net.weavemc.loader.api.event.SubscribeEvent;
import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.RGBSetting;
import ravenweave.client.module.setting.impl.SliderSetting;
import ravenweave.client.module.setting.impl.TickSetting;
import ravenweave.client.utils.Utils;

import java.awt.*;
import java.util.Iterator;

public class ChestESP extends Module {
    public static RGBSetting rgb;
    public static TickSetting rainbow;

    public ChestESP() {
        super("ChestESP", ModuleCategory.render);
        this.registerSetting(rgb = new RGBSetting("Red", 255, 255, 255));
        this.registerSetting(rainbow = new TickSetting("Rainbow", false));
    }

    @SubscribeEvent
    public void onForgeEvent(RenderWorldEvent fe) {
        if (!this.enabled) return;
        if (Utils.Player.isPlayerInGame()) {
            int color = rainbow.isToggled() ? Utils.Client.rainbowDraw(2L, 0L)
                    : (new Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue())).getRGB();
            Iterator var3 = mc.theWorld.loadedTileEntityList.iterator();

            while (true) {
                TileEntity te;
                do {
                    if (!var3.hasNext()) {
                        return;
                    }

                    te = (TileEntity) var3.next();
                } while (!(te instanceof TileEntityChest) && !(te instanceof TileEntityEnderChest));

                Utils.HUD.re(te.getPos(), color, true);
            }
        }
    }
}