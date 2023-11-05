package ravenweave.client.module.modules.combat;

import net.minecraft.entity.Entity;
import ravenweave.client.Raven;
import ravenweave.client.module.Module;
import ravenweave.client.module.modules.world.AntiBot;
import ravenweave.client.module.setting.impl.DescriptionSetting;
import ravenweave.client.module.setting.impl.SliderSetting;
import ravenweave.client.module.setting.impl.TickSetting;

public class HitBoxes extends Module {
    public static SliderSetting blocks;
    public static TickSetting vertical;

    public HitBoxes() {
        super("HitBoxes", ModuleCategory.combat);
        this.registerSetting(new DescriptionSetting("Changed from multiplier to extra blocks!"));
        this.registerSetting(blocks = new SliderSetting("Extra Blocks", 0.2D, 0.05D, 2.0D, 0.05D));
        this.registerSetting(vertical = new TickSetting("Vertical", false));
    }

    public static double expandHitbox(Entity en) {
        Module hitBox = Raven.moduleManager.getModuleByClazz(HitBoxes.class);
        return ((hitBox != null) && hitBox.isEnabled() && !AntiBot.bot(en)) ? blocks.getInput() : 0D;
    }
}