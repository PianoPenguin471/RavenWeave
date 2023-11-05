package ravenweave.client.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.weavemc.loader.api.event.CancellableEvent;

@Getter
@AllArgsConstructor
public class LivingUpdateEvent extends CancellableEvent {

    public Entity entity;
    public boolean sprinting;

    public EntityLivingBase getEntityLiving() {
        return (EntityLivingBase) entity;
    }

}
