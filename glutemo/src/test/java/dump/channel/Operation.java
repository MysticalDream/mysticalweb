package dump.channel;

/**
 * @author MysticalDream
 */
public interface Operation<T> {

    void setNext(Operation<T> next);

    void invoke(T data);

}
