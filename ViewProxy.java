import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

/**
 * ViewProxy class to receive UDP messages from ModelProxy
 * @author zmm2962
 */
public class ViewProxy {

	private DatagramSocket mailbox;
	private ViewListener viewListener;

	/**
	 * Create new ViewProxy
	 * @param mailbox
	 */
	public ViewProxy(DatagramSocket mailbox) {
		this.mailbox = mailbox;
	}

	/**
	 * Set the ViewListener.
	 * @param viewListener
	 *            View listener.
	 */
	public void setViewListener(ViewListener viewListener) {
		this.viewListener = viewListener;
		new ReaderThread().start();
	}

	/**
	 * Subclass to read messages coming in and handle them
	 * accordingly.
	 * @author zmm2962
	 */
	private class ReaderThread extends Thread {
		public void run() {
			byte[] payload = new byte[1024]; 
			try {
				for (;;) {
					DatagramPacket packet = new DatagramPacket(payload, payload.length);
					mailbox.receive(packet);
					SocketAddress address = packet.getSocketAddress();
					DataInputStream in = new DataInputStream(new ByteArrayInputStream(payload, 0, packet.getLength()));
					long timeStamp;
					int temperature;
					byte b = in.readByte();
					switch (b) {
					case 'R':
						timeStamp = in.readLong();
						temperature = in.readInt();
						viewListener.report(address, timeStamp, temperature);
						break;
					default:
						System.err.println("Bad message");
						break;
					}
				}
			} catch (IOException exc) {
				exc.printStackTrace(System.err);
				System.exit(1);
			}
		}
	}

}