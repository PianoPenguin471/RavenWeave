package ravenweave.client.event.impl;

import net.weavemc.loader.api.event.Event;

public class SlowdownEvent extends Event {
    float strafeSpeedMultiplier = 2.0F;
    float forwardSpeedMultiplier = 2.0F;

    public float getStrafeSpeedMultiplier() {
        return strafeSpeedMultiplier;
    }

    public float getForwardSpeedMultiplier() {
        return forwardSpeedMultiplier;
    }

    public void setStrafeSpeedMultiplier(float strafeSpeedMultiplier) {
        this.strafeSpeedMultiplier = strafeSpeedMultiplier;
    }

    public void setForwardSpeedMultiplier(float forwardSpeedMultiplier) {
        this.forwardSpeedMultiplier = forwardSpeedMultiplier;
    }
}
