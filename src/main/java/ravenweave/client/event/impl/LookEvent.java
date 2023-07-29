package ravenweave.client.event.impl;

import net.weavemc.loader.api.event.Event;

public class LookEvent extends Event {

	private float pitch, yaw;

	public LookEvent(float pitch, float yaw) {
	    this.pitch = pitch;
	    this.yaw = yaw;
    }

    public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

}
