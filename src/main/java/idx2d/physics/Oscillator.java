package idx2d.physics;

public class Oscillator
{
	double omega;
	double base, amplitude;
	double phi0=-Math.PI/2;
	
	public Oscillator(double min, double max, double periodMilliseconds)
	{
		amplitude=(max-min)/2;
		base=min+amplitude;
		omega=2.0*Math.PI/periodMilliseconds;
	}
	
	public double getValue(long timeMilliseconds)
	{
		return base+amplitude*Math.sin(phi0+omega*timeMilliseconds);
	}
}

