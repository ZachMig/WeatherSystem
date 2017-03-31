import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * Main server program for the Weather System.
 * @author zmm2962
 */
public class Monitor {

	/**
	 * Main method
	 * @param args 
	 * 			the hostname and port
	 * @throws Exception 
	 * 			any exception that might bubble up
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 2)
			usage();
		String serverhost = args[0];
		int serverport = Integer.parseInt(args[1]);

		DatagramSocket mailbox = new DatagramSocket(new InetSocketAddress(
				serverhost, serverport));
		
		final MonitorUI view = MonitorUI.create();
		
		ThermometerModel model = new ThermometerModel();
		model.setListener(new ModelListener() {
			/**
			 * Update the average temperature to be displayed.
			 * 
			 * @param temp
			 * 			The new average temperature.
			 */
			public void updateAverage(double temp){
				view.updateAverage(temp);
			}
		});

		ViewProxy proxy = new ViewProxy(mailbox);
		proxy.setViewListener(model);
	}

	/**
	 * Print a usage message and exit.
	 */
	private static void usage() {
		System.err.println("Usage: java FireAlarmClient <serverhost> <serverport>");
		System.exit(1);
	}

}