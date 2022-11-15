package com.mysticaldream.glutemo.channel;

/**
 * @author MysticalDream
 */
public interface Reactor extends ReactorGroup {

    ReactorGroup reactorGroup();

    boolean inReactor();

}
