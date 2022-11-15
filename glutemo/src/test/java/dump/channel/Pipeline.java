package dump.channel;

import java.util.Deque;
import java.util.LinkedList;

/**
 * @author MysticalDream
 */
public class Pipeline<T> implements Operation<T> {

    private Deque<Operation<T>> operationList = new LinkedList<>();

    private Operation<T> terminate;


    public Pipeline() {
        terminate = new FunctionOperation<>(data -> {
            System.out.println("terminate");
        });
    }

    public void registerOperation(Operation<T> operation) {

        operation.setNext(terminate);
        if (!operationList.isEmpty()) {
            operationList.getLast().setNext(operation);
        }
        operationList.add(operation);
    }

    @Override
    public void setNext(Operation<T> next) {
        terminate.setNext(next);
    }

    @Override
    public void invoke(T data) {
        Operation<T> operation = (!operationList.isEmpty()) ? operationList.getFirst() : terminate;
        operation.invoke(data);
    }


}
