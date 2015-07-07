package planetZoooom.utils;

import org.lwjgl.util.vector.Vector3f;

public class GameUtils 
{
	//public static ICamera currentCam;
	//public static Timer timer; //?
	
	public static float getDistanceBetween(Vector3f position1, Vector3f position2 )
	{
		Vector3f distance = new Vector3f();
		Vector3f.sub(position1, position2, distance);
		float result = Math.abs(distance.length());
		
		
		return result;
	}
	
	/**
	 * Gives a value between 0 and 1 that depends on the distance between a
	 * point and the sphere. Can be used to compute a subdivision that looks
	 * "okay" from a given point. The distance gets clamped to a max value.
	 * 
	 * @param distanceToSphere
	 * @return value between 0 and 1
	 */
	public static float getDistanceCoefficient(float distanceToSphere)
	{
		// distances over 100 don't affect the planets resolution
		float maxDistance = 100;
		distanceToSphere = distanceToSphere > maxDistance ? maxDistance
				: distanceToSphere;

		float subdivisionCoefficient = (maxDistance - distanceToSphere) / 100;

		// (100 - x) ^ 5 / (100 ^ 5)
		float curveSlope = 3f;
		subdivisionCoefficient = (float) (Math.pow(maxDistance
				- distanceToSphere, curveSlope) / Math.pow(maxDistance,
				curveSlope));

		return subdivisionCoefficient;
	}
}
