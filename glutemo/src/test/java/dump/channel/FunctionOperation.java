package dump.channel;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author MysticalDream
 */
public class FunctionOperation<T> implements Operation<T> {

    private Function<T, Boolean> action;

    private Operation<T> next;

    public FunctionOperation(Consumer<T> action) {

        this.action = data -> {
            action.accept(data);
            return true;
        };

    }

    public FunctionOperation(Function<T, Boolean> action) {
        this.action = action;
    }

    @Override
    public void setNext(Operation<T> next) {
        this.next = next;
    }

    @Override
    public void invoke(T data) {
        if (action.apply(data)) {
            if (next != null) {
                next.invoke(data);
            }
        }
    }
}
