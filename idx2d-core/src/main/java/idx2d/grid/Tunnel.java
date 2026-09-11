package idx2d.grid;

public class Tunnel
{
	private static double deg2rad=3.14159265/180;

	// P U B L I C   M E T H O D S

		public static Grid8x8 create(Grid8x8 grid, double rotation, double shift, double uscale, double vscale)
		{
			return create(grid,90,rotation,shift,uscale,vscale);	
		}
		
		public static Grid8x8 create(Grid8x8 grid, double fov, double rotation, double shift, double uscale, double vscale)
		{
			double fovfact=1/Math.tan(deg2rad*fov/2);
			double offset=(rotation*deg2rad)%(2*Math.PI);
			Grid8x8 newGrid=grid;
			double xpos,ypos,angle,dist;
			int dim=Math.min(grid.gridwidth,grid.gridheight);
			double centerX=0.5*grid.getUMax();
			double centerY=0.5*grid.getVMax();
			
			for(int j=0;j<grid.height;j++)
			{
				ypos=(double)j/dim-centerY;
				for (int i=0;i<grid.width;i++)
				{
					xpos=(double)i/dim-centerX;
					dist=Math.sqrt(xpos*xpos+ypos*ypos);
					angle=Math.atan2(xpos,ypos)/2/Math.PI;
					newGrid.node[i][j].v=angle;
					newGrid.node[i][j].u=fovfact/(dist+0.2);
					newGrid.node[i][j].intensity=(int)(255*dist*dist*4);
				}
				
			}
			newGrid.scaleUV(uscale,vscale);
			newGrid.addUV(shift%1,offset/2/Math.PI);
			
			return newGrid;
		}
		
}