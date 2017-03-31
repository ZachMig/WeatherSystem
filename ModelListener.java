/**
 * Interface for the ModelListeners
 * @author zmm2962
 */
public interface ModelListener {
	
	/**
	 * Update the temperature averaged from all valid sensors.
	 * @param avgTemp
	 * 			The averaged temperature
	 */
	public void updateAverage(double avgTemp);

}