package dump.channel;

/**
 * @author MysticalDream
 */
public class WriteOperation implements Operation<String> {

    private Operation<String> next;

    @Override
    public void setNext(Operation<String> next) {
        this.next = next;
    }

    @Override
    public void invoke(String data) {
        Thread t = new Thread(() -> {
            System.out.println("Writing data to the disk...");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Data successfully written to the disk !");
            if (next != null) {
                next.invoke(data);
            }

        });
        t.start();

    }
}
