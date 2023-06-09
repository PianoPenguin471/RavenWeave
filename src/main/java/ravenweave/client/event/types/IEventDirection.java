package ravenweave.client.event.types;

import ravenweave.client.event.EventDirection;

public interface IEventDirection {

    EventDirection getDirection();

    default boolean isIncoming() {
        return getDirection() == EventDirection.INCOMING;
    }

    default boolean isOutgoing() {
        return getDirection() == EventDirection.OUTGOING;
    }

}
