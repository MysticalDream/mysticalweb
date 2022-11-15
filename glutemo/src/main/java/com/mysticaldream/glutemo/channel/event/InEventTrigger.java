package com.mysticaldream.glutemo.channel.event;

/**
 * @author MysticalDream
 */
public interface InEventTrigger {

    InEventTrigger propagateRegisteredEvent();

    InEventTrigger propagateReadEvent(Object msg);

    InEventTrigger propagateExceptionCaughtEvent(Throwable throwable);
}

