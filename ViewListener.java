import java.io.IOException;
import java.net.SocketAddress;

/**
 * Interface for the ViewListeners
 * 
 * Temperature reporting functionality has been kept in one report method
 * 	rather than being split into two signatures. The result of this is that 
 * 	SensorUI will send a null SocketAddress to the ModelProxy, as it is redundant.
 *  This eliminates the need for report(long timeStamp, int temperature) signature,
 *  as each implementing class would then have one implemented method and one blank.
 *  
 * I believe that in a setting where this code had the potential to persist for a 
 * 	long time and needed to be able to scale up easily, it would be correct
 *  to split the method into two signatures. For the sake of this closed project, however,
 *  this implementation makes more sense and is cleaner to me. 
 * 
 * @author zmm2962
 */

public interface ViewListener {
	
	/**
	 * Report a temperature for the sensor associated with the given 
	 * 	SocketAddress, and reported at the time of timeStamp.
	 * @param address
	 * 			The SocketAdress of the sensor the report is coming from
	 * @param timeStamp
	 * 			The timestamp of the message being sent
	 * @param temperature
	 * 			The temperature reported
	 * @throws IOException
	 * 			Thrown in the case of an IO error.
	 */
	public void report(SocketAddress address, long timeStamp, int temperature) throws IOException;
}