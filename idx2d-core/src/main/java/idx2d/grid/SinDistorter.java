package idx2d.grid;

public class SinDistorter
{
	private static double deg2rad=3.14159265/180;

	// P U B L I C   M E T H O D S

		public static Grid8x8 distort(Grid8x8 grid, double xamp, double xperiod, double xphizero, double yamp, double yperiod, double yphizero)
		{
			Grid8x8 newGrid=new Grid8x8(grid.gridwidth*8,grid.gridheight*8);
			
			double dx,dy;
			for(int j=0;j<grid.height;j++)
			{
				dy=yamp*Math.sin(2.0*Math.PI*j/grid.height*yperiod+yphizero);
				for (int i=0;i<grid.width;i++)
				{
					dx=xamp*Math.sin(2.0*Math.PI*i/grid.width*xperiod+xphizero);
					newGrid.node[i][j].u=grid.node[i][j].u+dx;
					newGrid.node[i][j].v=grid.node[i][j].v+dy;
					newGrid.node[i][j].intensity=grid.node[i][j].intensity;
				}
			}
			return newGrid;
		}
}