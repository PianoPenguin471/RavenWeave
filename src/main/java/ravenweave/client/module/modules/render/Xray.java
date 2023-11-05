package ravenweave.client.module.modules.render;

import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.SliderSetting;
import ravenweave.client.module.setting.impl.TickSetting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockRedstoneOre;

public class Xray extends Module {
    public static Xray instance;

    public static TickSetting hypixel;
    public static SliderSetting opacity;

    public Xray() {
        super("Xray", ModuleCategory.render);
        this.registerSetting(opacity = new SliderSetting("Opacity", 120, 0, 255, 1));
        this.registerSetting(hypixel = new TickSetting("Hypixel", true));
        instance = this;
    }

    @Override
    public void onEnable() {
        mc.renderGlobal.loadRenderers();
    }

    @Override
    public void onDisable() {
        mc.renderGlobal.loadRenderers();
    }

    public static boolean isOreBlock(Block block) {
        return block instanceof BlockOre || block instanceof BlockRedstoneOre;
    }

}