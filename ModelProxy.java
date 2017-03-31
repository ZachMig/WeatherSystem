import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

/**
 * ModelProxy class to communicate client -> server
 * @author zmm2962
 */
public class ModelProxy implements ViewListener {

	private DatagramSocket mailbox;
	private SocketAddress destination;

	/**
	 * Create new ModelProxy
	 * @param mailbox
	 * 			The mailbox 
	 * @param destination
	 * 			Destination address to send to
	 */
	public ModelProxy(DatagramSocket mailbox, SocketAddress destination) {
		this.mailbox = mailbox;
		this.destination = destination;
	}
	
	/**
	 * Writes the reported temperature to a new DatagramPacket and
	 * 	sends it to the set mailbox
	 * @param address
	 * 			The SocketAddress, will be null here when called from SensorUI,
	 * 			 as the reading thread will be able to pull the SocketAddress from the packet.
	 * @param timeStamp
	 * 			The timestamp at which this temperature was reported.
	 * @param temperature
	 * 			The reported temperature
	 */
	public void report(SocketAddress address, long timeStamp, int temperature) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(baos);
		out.writeByte('R');
		out.writeLong(timeStamp);
		out.writeInt(temperature);
		out.close();
		byte[] payload = baos.toByteArray();
		mailbox.send(new DatagramPacket(payload, payload.length, destination));
	}

}
