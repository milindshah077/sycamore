/**
 * 
 */
package it.diunipi.volpi.sycamore.engine;

import it.diunipi.volpi.sycamore.plugins.algorithms.Algorithm;

import java.util.Vector;

/**
 * @author Vale
 * 
 */
public class ObservationExt<P extends SycamoreAbstractPoint & ComputablePoint<P>> extends Observation<P>
{
	private final Algorithm<P>	algorithm;

	/**
	 * @param robotPosition
	 * @param robotLights
	 * @param humanPilot
	 */
	public ObservationExt(P robotPosition, Vector<SycamoreObservedLight> robotLights, Algorithm<P> algorithm)
	{
		super(robotPosition, robotLights, algorithm.isHumanPilot());
		this.algorithm = algorithm;
	}
	
	/**
	 * @return the algorithm
	 */
	public Algorithm<P> getAlgorithm()
	{
		return algorithm;
	}

	/**
	 * @param observations
	 * @return
	 */
	public static <P extends SycamoreAbstractPoint & ComputablePoint<P>> Vector<P> getPositions(Vector<Observation<P>> observations)
	{
		Vector<P> ret = new Vector<P>();
		
		for (Observation<P> observation : observations)
		{
			ret.add(observation.getRobotPosition());
		}
		
		return ret;
	}

}
