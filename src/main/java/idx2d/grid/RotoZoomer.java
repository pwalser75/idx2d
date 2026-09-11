package idx2d.grid;

public class RotoZoomer
{
	private static double deg2rad=3.14159265/180;

	// P U B L I C   M E T H O D S

		public static Grid8x8 rotoZoom(Grid8x8 grid, double rotation, double zoom)
		{
			int dim=Math.min(grid.gridwidth,grid.gridwidth);
			return rotoZoom(grid, rotation, zoom, 0.5*grid.gridwidth/dim, 0.5*grid.gridwidth/dim);
		}

		public static Grid8x8 rotoZoom(Grid8x8 grid, double rotation, double zoom, double centerX, double centerY)
		{
			Grid8x8 newGrid=grid;
			double xpos,ypos;
			double sin=Math.sin(-rotation*deg2rad);
			double cos=Math.cos(-rotation*deg2rad);
			int dim=Math.min(grid.gridwidth,grid.gridheight);
			for(int j=0;j<grid.height;j++)
			{
				ypos=(double)j/dim-centerY;
				for (int i=0;i<grid.width;i++)
				{
					xpos=(double)i/dim-centerX;
					newGrid.node[i][j].u=centerX+zoom*(xpos*cos+ypos*sin);
					newGrid.node[i][j].v=centerY+zoom*(-xpos*sin+ypos*cos);
				}
			}
			return newGrid;
		}
}