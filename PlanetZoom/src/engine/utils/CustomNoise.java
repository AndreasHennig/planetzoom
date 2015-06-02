package engine.utils;

public class CustomNoise {
	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param octaves
	 * @param frequency
	 * @return Value between 0 and 1.
	 */
	public static double overlayNoise(double x, double y, double z, int octaves, double frequencyScale) {
		double noise = 0;
		int n = octaves - 1;
		
		for(double i = 0; i < n; i++) {
			double freq = Math.pow(2, i) * frequencyScale;
			noise += (float) ((engine.utils.SimplexNoise.noise(x * freq, y * freq, z * freq) + 1) / 2);
		}
		
		return noise / octaves;
	}
}
