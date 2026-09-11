package idx2d.grid;

public class Drop
{
	private static double deg2rad=3.14159265/180;

	// P U B L I C   M E T H O D S

		public static Grid8x8 create(Grid8x8 grid, double maxamp, double waves, double phase)
		{
			Grid8x8 newGrid=grid;
			double xpos,ypos,power,dist;
			double centerX=0.5*grid.getUMax();
			double centerY=0.5*grid.getVMax();
			double phi=System.currentTimeMillis();
			
			for(int j=0;j<grid.height;j++)
			{
				ypos=(double)j/grid.gridheight-centerY;
				for (int i=0;i<grid.width;i++)
				{
					xpos=(double)i/grid.gridwidth-centerX;
					dist=Math.sqrt(xpos*xpos+ypos*ypos);
					power=(1-dist)*maxamp*Math.sin(phase+3.14*waves*(1-dist));
					newGrid.node[i][j].u=grid.node[i][j].u+(xpos-centerX)*power;
					newGrid.node[i][j].v=grid.node[i][j].v+(ypos-centerY)*power;
											
					//newGrid.node[i][j].intensity=(int)(255*dist*dist*4);
				}
				
			}
			return newGrid;
		}
		
}