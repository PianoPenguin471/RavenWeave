package ravenweave.client.module.modules.player;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.*;
import net.weavemc.loader.api.event.RenderGameOverlayEvent;
import net.weavemc.loader.api.event.SubscribeEvent;
import org.lwjgl.input.Mouse;
import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.ComboSetting;
import ravenweave.client.module.setting.impl.DoubleSliderSetting;
import ravenweave.client.module.setting.impl.SliderSetting;
import ravenweave.client.module.setting.impl.TickSetting;
import ravenweave.client.utils.ReflectionUtils;
import ravenweave.client.utils.SoundUtils;
import ravenweave.client.utils.Utils;

import java.lang.reflect.Method;
import java.util.Random;

public class RightClicker extends Module {
    public static DoubleSliderSetting CPS;
    public static SliderSetting jitterRight;
    public static TickSetting onlyBlocks;
    public static ComboSetting<SoundMode> soundMode;

    private Random rand;
    private final Method playerMouseInput;
    private long righti;
    private long rightj;
    private long rightk;
    private long rightl;
    private double rightm;
    private boolean rightn;

    public RightClicker() {
        super("Right Clicker", ModuleCategory.player);

        this.registerSetting(CPS = new DoubleSliderSetting("CPS", 12, 16, 1, 24, 0.5));
        this.registerSetting(jitterRight = new SliderSetting("Jitter right", 0.0D, 0.0D, 3.0D, 0.1D));
        this.registerSetting(onlyBlocks = new TickSetting("Blocks only", false));
        this.registerSetting(soundMode = new ComboSetting<>("Click sound", SoundMode.NONE));

        try {
            this.playerMouseInput = ReflectionUtils.findMethod(GuiScreen.class, null,
                    "mouseClicked", Integer.TYPE, Integer.TYPE, Integer.TYPE);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        if (this.playerMouseInput != null) {
            this.playerMouseInput.setAccessible(true);
        }

    }

    @Override
    public void onEnable() {
        if (this.playerMouseInput == null) {
            this.disable();
        }

        this.rand = new Random();
    }

    @SubscribeEvent
    public void onRender2D(RenderGameOverlayEvent e) {
        ravenClick();
    }

    private void ravenClick() {
        if (!Utils.Player.isPlayerInGame())
            return;

        if (mc.currentScreen != null || !mc.inGameHasFocus)
            return;

        Mouse.poll();
        if (Mouse.isButtonDown(1)) {
            this.rightClickExecute(mc.gameSettings.keyBindUseItem.getKeyCode());
        } else if (!Mouse.isButtonDown(1)) {
            this.righti = 0L;
            this.rightj = 0L;
        }
    }

    public boolean rightClickAllowed() {
        ItemStack item = mc.thePlayer.getHeldItem();
        if (item != null) {
            if (onlyBlocks.isToggled()) {
                return item.getItem() instanceof ItemBlock;
            }
        }

        return true;
    }

    public void rightClickExecute(int key) {
        if (!this.rightClickAllowed())
            return;

        if (jitterRight.getInput() > 0.0D) {
            double jitterMultiplier = jitterRight.getInput() * 0.45D;
            EntityPlayerSP entityPlayer;
            if (this.rand.nextBoolean()) {
                entityPlayer = mc.thePlayer;
                entityPlayer.rotationYaw = (float) ((double) entityPlayer.rotationYaw
                        + (double) this.rand.nextFloat() * jitterMultiplier);
            } else {
                entityPlayer = mc.thePlayer;
                entityPlayer.rotationYaw = (float) ((double) entityPlayer.rotationYaw
                        - (double) this.rand.nextFloat() * jitterMultiplier);
            }

            if (this.rand.nextBoolean()) {
                entityPlayer = mc.thePlayer;
                entityPlayer.rotationPitch = (float) ((double) entityPlayer.rotationPitch
                        + (double) this.rand.nextFloat() * jitterMultiplier * 0.45D);
            } else {
                entityPlayer = mc.thePlayer;
                entityPlayer.rotationPitch = (float) ((double) entityPlayer.rotationPitch
                        - (double) this.rand.nextFloat() * jitterMultiplier * 0.45D);
            }
        }

        if (this.rightj > 0L && this.righti > 0L) {
            if (System.currentTimeMillis() > this.rightj) {
                if (soundMode.getMode() != SoundMode.NONE) {
                    if (SoundUtils.clip != null) SoundUtils.clip.stop();
                    SoundUtils.playSound(soundMode.getMode().name());
                }

                KeyBinding.setKeyBindState(key, true);
                KeyBinding.onTick(key);
                Utils.Client.setMouseButtonState(1, false);
                Utils.Client.setMouseButtonState(1, true);
                this.genRightTimings();
            } else if (System.currentTimeMillis() > this.righti) {
                KeyBinding.setKeyBindState(key, false);
                Utils.Client.setMouseButtonState(1, false);
            }
        } else {
            this.genRightTimings();
        }

    }

    public void genRightTimings() {
        double clickSpeed = Utils.Client.ranModuleVal(CPS, this.rand) + 0.4D * this.rand.nextDouble();
        long delay = (int) Math.round(1000.0D / clickSpeed);
        if (System.currentTimeMillis() > this.rightk) {
            if (!this.rightn && this.rand.nextInt(100) >= 85) {
                this.rightn = true;
                this.rightm = 1.1D + this.rand.nextDouble() * 0.15D;
            } else {
                this.rightn = false;
            }

            this.rightk = System.currentTimeMillis() + 500L + (long) this.rand.nextInt(1500);
        }

        if (this.rightn) {
            delay = (long) ((double) delay * this.rightm);
        }

        if (System.currentTimeMillis() > this.rightl) {
            if (this.rand.nextInt(100) >= 80) {
                delay += 50L + (long) this.rand.nextInt(100);
            }

            this.rightl = System.currentTimeMillis() + 500L + (long) this.rand.nextInt(1500);
        }

        this.rightj = System.currentTimeMillis() + delay;
        this.righti = System.currentTimeMillis() + delay / 2L - (long) this.rand.nextInt(10);
    }

    public enum SoundMode {
        NONE, BASIC, CLICK, DOUBLE, G303, G502, GPRO, HP, MICROSOFT, OLD
    }
}