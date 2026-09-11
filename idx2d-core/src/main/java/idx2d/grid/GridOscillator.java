package idx2d.grid;

public class GridOscillator
{
	private final static double deg2rad=3.14159265/180;
	
	private GridOscillator()
	{
	}

	public static void oscillate(Grid8x8 grid, double aggregation,double damping)
	{

		for(int i=0;i<grid.width;i++)
		{
			grid.node[i][0].z=0;
			grid.node[i][0].nx=grid.node[i][0].ny=127;
		}
		for(int i=0;i<grid.width;i++)
		{
			grid.node[i][grid.height-1].z=0;
			grid.node[i][grid.height-1].nx=grid.node[i][grid.height-1].ny=127;
		}
		for(int i=0;i<grid.height;i++)
		{
			grid.node[0][i].z=0;
			grid.node[0][i].nx=grid.node[0][i].ny=127;
		}
		for(int i=0;i<grid.height;i++)
		{
			grid.node[grid.width-1][i].z=0;
			grid.node[grid.width-1][i].nx=grid.node[grid.width-1][i].ny=127;
		}
		


		for(int j=1;j<grid.height-1;j++)
			for (int i=1;i<grid.width-1;i++)
				grid.node[i][j].velocity+=-aggregation*(grid.node[i][j].z-(grid.node[i-1][j].z+grid.node[i+1][j].z+grid.node[i][j-1].z+grid.node[i][j+1].z)/4);


		for(int j=1;j<grid.height-1;j++)
			for (int i=1;i<grid.width-1;i++)
			{
				grid.node[i][j].z+=grid.node[i][j].velocity/2;
				grid.node[i][j].z*=damping;
			}
			
		for(int j=1;j<grid.height-1;j++)
			for (int i=1;i<grid.width-1;i++)
			{
				grid.node[i][j].z+=grid.node[i][j].velocity;
				grid.node[i][j].z*=damping;
			}
			
		for(int j=1;j<grid.height-1;j++)
			for (int i=1;i<grid.width-1;i++)
			{
				grid.node[i][j].nx=(int)(127+128*(grid.node[i-1][j].z-grid.node[i+1][j].z));
				grid.node[i][j].ny=(int)(127+128*(grid.node[i][j-1].z-grid.node[i][j+1].z));
				grid.node[i][j].nx=crop(grid.node[i][j].nx,0,255);
				grid.node[i][j].ny=crop(grid.node[i][j].ny,0,255);
			}
					
	}
	
	private static int crop(int a, int min, int max)
	{
		return (a<min)?min:(a>max)?max:a;
	}
}