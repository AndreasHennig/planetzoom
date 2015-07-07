package planetZoooom.utils;

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
	 * By changing the lambda of the first octave we can control the size of the continents.
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
	public static double perlinNoise(double x, double y, double z, 
									 int octaves, double lambda, double amplitude) {
		double sum = 0;
		double maxAmp = 0;
		double poweredOctave;
		double octaveAmplitude;
		double octaveLambda;
		double add;
		
		for(double octave = 1; octave <= octaves; octave++) {
			// speed up 2^x via bit shift?
			poweredOctave = Math.pow(2, octave);
			
			octaveAmplitude = amplitude / poweredOctave;
			octaveLambda = lambda / poweredOctave;
			
			// TODO: Perlin Noise Ridged Multifractal
			
			// Perlin Noise Ridget
//			add = (engine.utils.SimplexNoise.noise(x / octaveLambda, y / octaveLambda, z / octaveLambda));
//			
//			System.out.println(add);
//			
//			if (add > 0) {
//				add = -1 * add + 1;
//			} else {
//				add = add + 1;
//			}

			// Perlin Noise Multi Fractal
			add = (planetZoooom.utils.SimplexNoise.noise(x / octaveLambda, y / octaveLambda, z / octaveLambda) * octaveAmplitude);

			if (octave > 1) {
				// Add *= [constant] * Sum;
				// Konstante wird verwendet um den Bereich der Anhebung zu steuern
				add = add * 1.75 * sum;
				

				maxAmp += octaveAmplitude * 1 * (sum != 0 ? sum : 1);
			} else {
				maxAmp += octaveAmplitude;
			}
			
			sum += add;
			
			//sum += (add);
		}
		
		// TODO: Aus irgend einem Grund ist der Berechnete Wert nicht genau zwischen -1 und 1
		// Workarround -> Clamp
		if(sum > 1)
			sum = 1;
		else if(sum < -1)
			sum = -1;
		
		return sum;
	}
}
