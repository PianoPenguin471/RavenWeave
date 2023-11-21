package ravenweave.client.module.modules.player;

import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.weavemc.loader.api.event.RenderWorldEvent;
import net.weavemc.loader.api.event.SubscribeEvent;
import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.*;
import ravenweave.client.utils.Utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BedAura extends Module {
    public static SliderSetting rangeInput;
    public static ComboSetting<BypassMode> bypassMode;
    public static SliderSetting bypassDistance;
    public static TickSetting disableOnBreak, rainbow;
    public static RGBSetting rgb;
    public static State state = State.LOOKING_FOR_BED;
    private boolean notifiedSearching = false, notifiedBreaking = false, notifiedBypassing = false;
    private Timer timer;
    private BlockPos bedPos;

    public BedAura() {
        super("BedAura", ModuleCategory.player);
        this.registerSetting(new DescriptionSetting("Might silent flag on Hypixel."));
        this.registerSetting(rangeInput = new SliderSetting("Range", 5.0D, 2.0D, 10.0D, 1.0D));
        this.registerSetting(bypassMode = new ComboSetting<>("Bypass Mode", BypassMode.NONE));
        this.registerSetting(bypassDistance = new SliderSetting("Bypass blocks", 1, 1, 5, 1));
        this.registerSetting(disableOnBreak = new TickSetting("Disable after break", true));
        this.registerSetting(rgb = new RGBSetting("Color", 255, 255, 255));
        this.registerSetting(rainbow = new TickSetting("Rainbow", false));
    }

    public void onEnable() {
        reset();
        (this.timer = new Timer()).scheduleAtFixedRate(this.timerTask(), 0L, 60L);
    }

    public void reset() {
        state = State.LOOKING_FOR_BED;
        this.notifiedBreaking = false;
        this.notifiedBypassing = false;
        this.notifiedSearching = false;
        this.bedPos = null;
    }

    public void onDisable() {
        if (this.timer != null) {
            this.timer.cancel();
            this.timer.purge();
            this.timer = null;
        }
        reset();
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldEvent event) {
        if (bedPos == null) return;
        int color = rainbow.isToggled() ? Utils.Client.rainbowDraw(2L, 0L) : (new Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue())).getRGB();
        if (state == State.BYPASS) {

            Utils.HUD.renderBlock(bedPos.up(), color, false);
        } else if (state == State.BREAKING_BED) {
            Utils.HUD.renderBlock(bedPos, color, false);
        }
    }

    public BlockPos getTopDefenseBlock() {
        for (int i = (int) bypassDistance.getInput(); i > 0; i--) {
            BlockPos pos = BedAura.this.bedPos.up(i);
            if (mc.theWorld.getBlockState(pos).getBlock() != Blocks.air) {
                return pos;
            }
        }
        return null;
    }


    public TimerTask timerTask() {
        return new TimerTask() {
            public void run() {
                // Don't bother breaking a bed if we're not even in a game
                if (!Utils.Player.isPlayerInGame()) return;
                switch (state) {
                    case LOOKING_FOR_BED -> {
                        if (!notifiedSearching) {
                            Utils.Player.sendMessageToSelf("Looking for valid bed block");
                            notifiedSearching = true;
                        }
                        BedAura.this.bedPos = getClosestBedPosition((int) rangeInput.getInput());
                        // We'll check again next time
                        if (BedAura.this.bedPos != null) {
                            Utils.Player.sendMessageToSelf("Found bed block at " + bedPos);
                            if (bypassMode.getMode() == BypassMode.NONE) {
                                state = State.BREAKING_BED;
                            } else {
                                state = State.BYPASS;
                            }
                        }
                    }
                    case BYPASS -> {
                        if (bypassMode.getMode() == BypassMode.BLOCK_ABOVE) {
                            // Get block above the bed
                            BlockPos blockAbove = getTopDefenseBlock();

                            // If the bed is exposed, move on
                            if (blockAbove == null) {
                                state = State.BREAKING_BED;
                                break;
                            }

                            // Break the block
                            mineBlock(blockAbove);
                            if (!notifiedBypassing) {
                                Utils.Player.sendMessageToSelf("Mining Bypass Block");
                                notifiedBypassing = true;
                            }
                        }
                    }
                    case BREAKING_BED -> {
                        mineBlock(BedAura.this.bedPos);
                        if (!notifiedBreaking) {
                            Utils.Player.sendMessageToSelf("Mining Bed Block");
                            notifiedBreaking = true;
                        }
                    }
                }
                updateState();
            }
        };
    }

    public void updateState() {
        if (this.bedPos == null) state = State.LOOKING_FOR_BED;
        else if (mc.theWorld.getBlockState(bedPos).getBlock() != Blocks.bed) {
            Utils.Player.sendMessageToSelf("Broke bed");
            if (disableOnBreak.isToggled()) this.disable();
            state = State.LOOKING_FOR_BED;
        }
    }

    public BlockPos getClosestBedPosition(int range) {
        List<BlockPos> bedPositions = new ArrayList<>();
        for (int y = range; y >= -range; --y) {
            for (int x = -range; x <= range; ++x) {
                for (int z = -range; z <= range; ++z) {
                    // Get BlockPos objects for all bed blocks around us
                    BlockPos pos = new BlockPos(Module.mc.thePlayer.posX + (double) x, mc.thePlayer.posY + (double) y, mc.thePlayer.posZ + (double) z);
                    IBlockState blockState = mc.theWorld.getBlockState(pos);
                    boolean isBed = blockState.getBlock() == Blocks.bed;

                    // Ignore block if it isn't a bed
                    if (!isBed) continue;

                    // Only break the foot of the bed (to simplify calculations and bypasses later)
                    if (blockState.getValue(BlockBed.PART) == BlockBed.EnumPartType.HEAD) continue;

                    bedPositions.add(pos);
                }
            }
        }
        if (bedPositions.isEmpty()) return null;
        bedPositions.sort((o1, o2) -> (int) (mc.thePlayer.getDistanceSq(o2) - mc.thePlayer.getDistanceSq(o1)));
        return bedPositions.get(0);
    }

    private void mineBlock(BlockPos pos) {
        mc.playerController.onPlayerDamageBlock(pos, EnumFacing.NORTH);
        //mc.playerController.clickBlock(pos, EnumFacing.NORTH);
        mc.thePlayer.swingItem();
        /*
        mc.thePlayer.sendQueue
                .addToSendQueue(new C07PacketPlayerDigging(Action.START_DESTROY_BLOCK, pos, EnumFacing.NORTH));
        mc.thePlayer.sendQueue
                .addToSendQueue(new C07PacketPlayerDigging(Action.STOP_DESTROY_BLOCK, pos, EnumFacing.NORTH));
         */
    }
    public enum State {
        LOOKING_FOR_BED,
        BYPASS,
        BREAKING_BED
    }

    public enum BypassMode {
        NONE,
        BLOCK_ABOVE
    }
}
