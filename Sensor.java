import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * Main client program of the Weather System. Multiple can be run to represent
 *  multiple distinct temperature sensors.
 * @author zmm2962
 */
public class Sensor {
	
	/**
	 * Main method
	 * @param args 
	 * 			the hostname and port of the server, plus those of the client
	 * @throws Exception 
	 * 			any exception that might bubble up
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 4)
			usage();
		String serverhost = args[0];
		int serverport = Integer.parseInt(args[1]);
		String clienthost = args[2];
		int clientport = Integer.parseInt(args[3]);

		DatagramSocket mailbox = new DatagramSocket(new InetSocketAddress(
				clienthost, clientport));

		SensorUI view = SensorUI.create();
		ModelProxy proxy = new ModelProxy(mailbox, new InetSocketAddress(
				serverhost, serverport));
		view.setViewListener(proxy);
	}

	/**
	 * Print a usage message and exit.
	 */
	private static void usage() {
		System.err.println("Usage: java Sensor <serverhost> <serverport> <clienthost> <clientport>");
		System.exit(1);
	}

}