package ravenweave.client.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.weavemc.loader.api.event.CancellableEvent;

@Getter
@AllArgsConstructor
public class RenderLabelEvent extends CancellableEvent {
    private final Entity target;
    private final double x;
    private final double y;
    private final double z;
}
