package ravenweave.client.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.weavemc.api.event.Event;

@Getter
@Setter
@AllArgsConstructor
public class SlowdownEvent extends Event {
    float strafeSpeedMultiplier, forwardSpeedMultiplier;
}
