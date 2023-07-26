package ravenweave.client.event.impl;

import ravenweave.client.event.types.Event;

public final class HitSlowDownEvent extends Event {
    public double slowDown;
    public boolean sprinting;

    public HitSlowDownEvent(double v, boolean b) {
        this.slowDown = v;
        this.sprinting = b;
    }

    public double getSlowDown() {
        return slowDown;
    }

    public void setSlowDown(double slowDown) {
        this.slowDown = slowDown;
    }

    public boolean isSprinting() {
        return sprinting;
    }

    public void setSprinting(boolean sprinting) {
        this.sprinting = sprinting;
    }
}
