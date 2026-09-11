package idx2d.grid;

public class GridNode
{
	//Texture coordinates (relative)
		public double u=0;
		public double v=0;
		public double z=0;
		public int intensity=255;

	//Texture coordinates (absolute)
		public int tx=0;
		public int ty=0;
		
	//Normal vector
		public int nx=0;
		public int ny=0;
		
	// Dynamics
		public double velocity=0;

}