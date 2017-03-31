import java.net.SocketAddress;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Class to represent the gathered data from each temperature sensor.
 * Holds and handles each sensor individually.
 * @author zmm2962
 */
public class ThermometerModel implements ViewListener {

	private ModelListener modelListener;
	private ScheduledExecutorService pool;
	private HashMap<SocketAddress, TempSensorModel> sensors;
	private static HashMap<SocketAddress, Integer> temperatures;
	private static double lastTempSum;
	
	/**
	 * A single temperature sensor
	 * @author zmm2962
	 */
	private class TempSensorModel {
		private int temp, countdown = 1;
		private long lastTimeStamp, timeoutNum;
		private ScheduledFuture<?> timeout;
		private SocketAddress address;
		
		/**
		 * Create a new sensor 
		 * @param address
		 * 			The SocketAddress of the sensor, to identify it
		 * @param temp
		 * 			The temperature the sensor has reported
		 * @param timeStamp 
		 * 			The timestamp of the last valid message from this sensor
		 */
		public TempSensorModel(SocketAddress address, int temp, long timeStamp) {
			this.address = address;
			this.temp = temp;
			this.lastTimeStamp = timeStamp;
		}
		
		/**
		 * Report a new temperature from this sensor
		 * @param newTemp
		 * 			The newly reported temperature
		 */
		public synchronized void report(int newTemp) {
			if (timeout != null)
				timeout.cancel(false);
			++timeoutNum;
			timeout = pool.schedule(new Runnable() {
				private long num = timeoutNum;
				public void run() {
					commFail(num);
				}
			}, 5, TimeUnit.SECONDS);

			if (newTemp != temp) {
				temp = newTemp;
				countdown = 1;
			} else if (countdown > 0) {
				--countdown;
				if (countdown == 0) {
					if (temperatures.containsKey(address)) {
						int oldTemp = temperatures.get(address);
						temperatures.put(address, temp); 
						updateAverage(temp - oldTemp);
					} else {
						temperatures.put(address, temp);
						updateAverage(temp);
					}
				}
			}
		}

		
		/**
		 * Get the lastTimeStamp from this sensor
		 * @return
		 * 			The last time stamp this sensor received
		 */
		public long getLastTimeStamp() {
			return this.lastTimeStamp;
		}
		
		/**
		 * Handle a communication failure by removing the temperature from the
		 * calculations and adjusting the average.
		 * @param num
		 * 			The timeout id
		 */
		private synchronized void commFail(long num) {
			if (num != timeoutNum)
				return;

			countdown = 1;
			temperatures.remove(this.address);
			updateAverage((-1) * this.temp);
		}
	}

	/**
	 * Create a new ThermometerModel
	 */
	public ThermometerModel() {
		pool = Executors.newScheduledThreadPool(1);
		sensors = new HashMap<SocketAddress, TempSensorModel>();
		temperatures = new HashMap<SocketAddress, Integer>();
		lastTempSum = 0;
	}

	/**
	 * Set the ModelListener
	 * @param modelListener
	 * 			The ModelListener
	 */
	public synchronized void setListener(ModelListener modelListener) {
		this.modelListener = modelListener;
	}
	
	/**
	 * Report a new temperature from some sensor
	 * @param address
	 * 			The SocketAddress used to identify the sensor.
	 * @param timeStamp
	 * 			The timestamp of the time at which the message was sent.
	 * @param temp
	 * 			The new temperature
	 */
	public synchronized void report(SocketAddress address, long timeStamp, int temp) {
		TempSensorModel sensor = sensors.get(address);
		if (sensor == null) {
			sensor = new TempSensorModel(address, temp, timeStamp);
			sensors.put(address, sensor);
			sensor.report(temp);
		} else {
			//Check if the timestamp of this message is earlier than lastTimeStamp
			if (sensor.getLastTimeStamp() > timeStamp) {
				return;
			} else {
				sensor.report(temp);
			}
		}
	}

	/**
	 * Update the average calculated temperature and send it to the 
	 * MonitorUI for display. By keeping 'lastTempSum' we avoid having to
	 * re-sum every time, as the UDP messages come in frequently.
	 * @param temperatureDifference
	 * 			The difference in temperature
	 */
	public synchronized void updateAverage(int temperatureDifference) {
		lastTempSum += temperatureDifference;
		if(lastTempSum == 0) {
			modelListener.updateAverage(0); 
		} else {
			modelListener.updateAverage(lastTempSum/temperatures.size()); 
		}
	}

}
