package ravenweave.client.module.modules.player;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemStack;
import net.weavemc.loader.api.event.SubscribeEvent;
import net.weavemc.loader.api.event.TickEvent;
import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.SliderSetting;
import ravenweave.client.module.setting.impl.TickSetting;
import ravenweave.client.utils.ReflectionUtils;
import ravenweave.client.utils.Utils;

import java.lang.reflect.Field;

public class NoJumpDelay extends Module {
    public static final Field jumpTicksField;

    static {
        jumpTicksField = ReflectionUtils.findField(EntityLivingBase.class, "jumpTicks");

        if (jumpTicksField != null) {
            jumpTicksField.setAccessible(true);
        }
    }

    public NoJumpDelay() {
        super("NoJumpDelay", ModuleCategory.player);
    }

    @Override
    public boolean canBeEnabled() {
        return jumpTicksField != null;
    }

    @SubscribeEvent
    public void onTick(TickEvent e) {
        if (!Utils.Player.isPlayerInGame() || !mc.inGameHasFocus || jumpTicksField == null) return;
        try {
            jumpTicksField.set(mc.thePlayer, 0);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }
}
