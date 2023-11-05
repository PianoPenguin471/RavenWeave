package ravenweave.client.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.weavemc.loader.api.event.Event;

@Getter
@AllArgsConstructor
public class AttackEntityEvent extends Event {

    public Entity target;
    public EntityPlayer playerIn;

}
