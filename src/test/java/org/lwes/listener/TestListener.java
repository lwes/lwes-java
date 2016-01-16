import static java.lang.System.out;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.junit.Ignore;
import org.lwes.listener.DatagramEventListener;
import org.lwes.listener.EventHandler;

/*
 * Not a unit test. Demonstrates thread leaks in threaded listeners. 
 * Before reaching 100 will be unable to spawn new threads with default JVM settings.
 * Exception in thread "main" java.lang.OutOfMemoryError: unable to create new native thread
 */

@Ignore
public class TestListener {

    public static void main(String[] args) throws InterruptedException, UnknownHostException {
	int count = 0;
	while (true) {
	    DatagramEventListener listener;
            listener = new DatagramEventListener();
            listener.setAddress(InetAddress.getByName("224.0.0.69"));
            listener.setPort(9191);
            listener.setQueueSize(50000);
	    listener.initialize();
	    out.println(count);
	    Thread.currentThread().sleep(1000);
	    listener.shutdown();
	    count++;
	}
    }

}
