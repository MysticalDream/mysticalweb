import dump.channel.FunctionOperation;
import dump.channel.Pipeline;
import dump.channel.WriteOperation;
import org.junit.jupiter.api.Test;

/**
 * @author MysticalDream
 */
public class ChannelPipelineTest {


    @Test
    public void testPipeline() {

        Pipeline<String> pipeline = new Pipeline<>();

        pipeline.registerOperation(new FunctionOperation<>(data -> {
            System.out.println("Everyone likes " + data);
            return true;
        }));

        pipeline.registerOperation(new WriteOperation());

        pipeline.registerOperation(new FunctionOperation<>(data -> {
            if (data.equals("banana")) {
                System.out.println("This banana made the pipeline abort...");
                return false;
            }
            return true;
        }));

        pipeline.registerOperation(new FunctionOperation<>(data -> {
            System.out.println("This operation should not be called !");
        }));

        Pipeline<String> verbose = new Pipeline<>();

        verbose.registerOperation(new FunctionOperation<>(data -> {
            System.out.println("Beginning of the pipeline...");
        }));
        verbose.registerOperation(pipeline);

        verbose.registerOperation(new FunctionOperation<>(data -> {
            System.out.println("End of the pipeline...");
        }));

        verbose.invoke("banana");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}
