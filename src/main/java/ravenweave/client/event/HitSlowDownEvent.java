package ravenweave.client.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.weavemc.api.event.Event;

@Getter
@Setter
@AllArgsConstructor
public class HitSlowDownEvent extends Event {

    public double slowDown;
    public boolean sprinting;

}