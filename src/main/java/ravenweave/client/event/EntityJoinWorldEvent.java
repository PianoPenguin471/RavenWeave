package ravenweave.client.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.weavemc.loader.api.event.Event;

@Getter
@AllArgsConstructor
public class EntityJoinWorldEvent extends Event {
    public Entity entity;
    public World world;
}
