package me.PianoPenguin471.events;

import net.weavemc.loader.api.event.Event;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class EntityJoinWorldEvent extends Event {
    public Entity entity;
    public World world;
    public EntityJoinWorldEvent(Entity entity, World worldObj) {
        this.entity = entity;
        this.world = worldObj;
    }
}
