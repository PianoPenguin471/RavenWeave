package keystrokesmod.client.module.modules.render;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import keystrokesmod.client.event.impl.TickEvent;
import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.setting.impl.TickSetting;

import keystrokesmod.client.utils.RenderUtils;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

import net.weavemc.loader.api.event.RenderWorldEvent;
import net.weavemc.loader.api.event.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class BlockESP extends Module {
    private final List<BlockESPBlock> blockList;
    private int searchTicks;
    private final TickSetting coal;
    private final TickSetting bed;
    private final TickSetting obsd;
    private final TickSetting iron;
    private final TickSetting redstone;
    private final TickSetting emerald;
    private final TickSetting diamond;
    private final TickSetting gold;
    private final TickSetting lapis;

    public BlockESP() {
        super("BlockESP", Module.ModuleCategory.render);
        this.blockList = new ArrayList<>();
        this.registerSetting(emerald = new TickSetting("Emerald Ore", false));
        this.registerSetting(diamond = new TickSetting("Diamond Ore", false));
        this.registerSetting(gold = new TickSetting("Gold Ore", false));
        this.registerSetting(lapis = new TickSetting("Lapis Lazuli Ore", false));
        this.registerSetting(redstone = new TickSetting("Redstone Ore", false));
        this.registerSetting(iron = new TickSetting("Iron Ore", false));
        this.registerSetting(coal = new TickSetting("Coal Ore", false));
        this.registerSetting(obsd = new TickSetting("Obsidian", true));
        this.registerSetting(bed = new TickSetting("Bed", true));
    }

    @Override
    public void onEnable() {
        this.search();
    }

    @Override
    public void onDisable() {
        this.blockList.clear();
        this.searchTicks = 0;
    }

    @SubscribeEvent
    public void onTick(TickEvent e) {
        if (Raven.mc.theWorld == null) {
            this.searchTicks = 0;
            return;
        }
        if (++this.searchTicks == 600) {
            this.searchTicks = 0;
            this.search();
        }
    }

    @SubscribeEvent
    public void onForgeEvent(final RenderWorldEvent event) {
        if (this.blockList.isEmpty()) {
            return;
        }
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glLineWidth(1.0f);
        for (final BlockESPBlock block : this.blockList) {
            final Color color = block.getColor();
            GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
            final double x = block.getX() - getRenderPosX();
            final double y = block.getY() - getRenderPosY();
            final double z = block.getZ() - getRenderPosZ();
            RenderUtils.drawOutlinedBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0));
        }
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }

    private void search() {
        this.blockList.clear();
        final EntityPlayerSP player = Raven.mc.thePlayer;
        final WorldClient world = Raven.mc.theWorld;
        int y;
        for (int range = y = 100; y >= -range; --y) {
            for (int x = range; x >= -range; --x) {
                for (int z = range; z >= -range; --z) {
                    final int posX = (int)(player.posX + x);
                    final int posY = (int)(player.posY + y);
                    final int posZ = (int)(player.posZ + z);
                    final Block block = world.getBlockState(new BlockPos(posX, posY, posZ)).getBlock();
                    final int id = Block.getIdFromBlock(block);
                    if (id == 129 && this.emerald.isToggled()) {
                        this.blockList.add(new BlockESPBlock(posX, posY, posZ, Color.GREEN));
                    } else if (id == 56 && this.diamond.isToggled()) {
                        this.blockList.add(new BlockESPBlock(posX, posY, posZ, Color.CYAN));
                    } else if (id == 14 && this.gold.isToggled()) {
                        this.blockList.add(new BlockESPBlock(posX, posY, posZ, Color.YELLOW));
                    } else if (id == 21 && this.lapis.isToggled()) {
                        this.blockList.add(new BlockESPBlock(posX, posY, posZ, Color.BLUE));
                    } else if ((id == 73 || id == 74) && this.redstone.isToggled()) {
                        this.blockList.add(new BlockESPBlock(posX, posY, posZ, Color.RED));
                    } else if (id == 15 && this.iron.isToggled()) {
                        this.blockList.add(new BlockESPBlock(posX, posY, posZ, Color.PINK));
                    } else if (id == 16 && this.coal.isToggled()) {
                        this.blockList.add(new BlockESPBlock(posX, posY, posZ, Color.BLACK));
                    } else if (id == 49 && this.obsd.isToggled()) {
                        this.blockList.add(new BlockESPBlock(posX, posY, posZ, new Color(255, 0, 255)));
                    } else if (id == 26 && this.bed.isToggled()) {
                        this.blockList.add(new BlockESPBlock(posX, posY, posZ, Color.CYAN));
                    }
                }
            }
        }
    }

    private double getRenderPosX() {
        return getFieldByReflection(RenderManager.class, Raven.mc.getRenderManager(), "renderPosX");
    }

    private double getRenderPosY() {
        return getFieldByReflection(RenderManager.class, Raven.mc.getRenderManager(), "renderPosY");
    }

    private double getRenderPosZ() {
        return getFieldByReflection(RenderManager.class, Raven.mc.getRenderManager(), "renderPosZ");
    }

    public static <T, E> T getFieldByReflection(final Class<? super E> classToAccess, final E instance, final String... fieldNames) {
        Field field = null;
        for (final String fieldName : fieldNames) {
            try {
                field = classToAccess.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ex) {
            }
            if (field != null) {
                break;
            }
        }
        if (field != null) {
            field.setAccessible(true);
            T fieldT = null;
            try {
                fieldT = (T) field.get(instance);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
            }
            return fieldT;
        }
        return null;
    }

    private class BlockESPBlock {
        private final int x;
        private final int y;
        private final int z;
        private final Color color;

        BlockESPBlock(final int x, final int y, final int z, final Color color) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.color = color;
        }

        private int getX() {
            return this.x;
        }

        private int getY() {
            return this.y;
        }

        private int getZ() {
            return this.z;
        }

        private Color getColor() {
            return this.color;
        }
    }
}
