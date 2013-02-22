package votingSystem.cTF;
/**
 * Adapted from http://tutorials.jenkov.com/java-multithreaded-servers/thread-pooled-server.html
 * @author test
 *
 */
public class Main {
	public static void main(String [ ] args)
	{
		CTFServer server = new CTFServer(9000);
		new Thread(server).start();
	
		try {
		    Thread.sleep(20 * 1000);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
		System.out.println("Stopping Server");
		server.stop();
	}
}
