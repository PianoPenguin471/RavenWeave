package me.PianoPenguin471.events;

import net.minecraft.util.BlockPos;
import net.weavemc.loader.api.event.Event;

public class ClickBlockEvent extends Event {
    private final BlockPos clickedBlock;

    public ClickBlockEvent(BlockPos clickedBlock) {
        this.clickedBlock = clickedBlock;
    }

    public BlockPos getClickedBlock() {
        return this.clickedBlock;
    }
}
