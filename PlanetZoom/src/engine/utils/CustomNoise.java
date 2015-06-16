package engine.utils;

public class CustomNoise {
	/**
	 * 
	 * Lambda
	 * = Wellenlaenge der Noise-Funktion; Formel: 1 / Frequenz
	 * 
	 * Frequenz verdoppeln = Wellenlaenge halbieren
	 * 
	 * Lacunarity ("Lueckenhaftigkeit")
	 * = Wie verhaelt sich die Frequenz der aktuellen Octave
	 * zur Frequenz der letzten Oktave; Haeufig 2
	 * 
	 * Persistence 
	 * = um wieviel wird die Amplitude in der aktuellen Oktave
	 * Vergleich zur letzten Oktave verringert.
	 * Kleinere Werte -> "raueres" Rauschen
	 * Hoehere Werte -> "weicheres" Rauschen
	 * 
	 * Fractal Dimension
	 * wtf?!
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param octaves
	 * @param frequency
	 * @return Value between 0 and 1.
	 */
	public static double perlinNoise(double x, double y, double z, int octaves, double frequencyScale) {
		double noise = 0;
		int n = octaves - 1;
		
		for(double i = 0; i < n; i++) {
			double lacunarity = Math.pow(2, i);
			double persistence = i / 2;
			noise += (float) (engine.utils.SimplexNoise.noise(x * lacunarity, y * lacunarity, z * lacunarity) / persistence);
		}
		
		return noise;
	}
}
