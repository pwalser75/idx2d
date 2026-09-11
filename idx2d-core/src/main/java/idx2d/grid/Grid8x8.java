package idx2d.grid;

import idx2d.*;

public class Grid8x8 implements Cloneable
{
	public int width;
	public int height;
	public int gridwidth;
	public int gridheight;
	public GridNode node[][];

	public Grid8x8(int w, int h)
	{
		width=w/8+1;
		height=h/8+1;
		gridwidth=width-1;
		gridheight=height-1;
		node=new GridNode[width][height];
		for (int j=height-1;j>=0;j--)
			for (int i=width-1;i>=0;i--)
				node[i][j]=new GridNode();
		reset();
	}

	public void reset()
	{
		int dim=Math.min(gridwidth,gridheight);
		for (int j=height-1;j>=0;j--)
			for (int i=width-1;i>=0;i--)
			{
				node[i][j].u=(double)i/dim;
				node[i][j].v=(double)j/dim;
				node[i][j].intensity=255;
			}
	}
	
	public double getUMax()
	{
		return (double)width/Math.min(gridwidth,gridheight);
	}
	
	public double getVMax()
	{
		return (double)height/Math.min(gridwidth,gridheight);
	}
	
	
	public void convert(Texture t)
	{
		int dim=Math.min(t.width,t.height);
		double xscale=65536d*dim;
		double yscale=65536d*dim;
		double zoom;
		for (int j=height-1;j>=0;j--)
			for (int i=width-1;i>=0;i--)
			{
				zoom=4/(4+node[i][j].z);
				node[i][j].tx=(int)(((node[i][j].u-0.5)*zoom+0.5)*xscale);
				node[i][j].ty=(int)(((node[i][j].v-0.5)*zoom+0.5)*yscale);
				node[i][j].intensity=crop(node[i][j].intensity,0,255);
			}
	}
	
	public Grid8x8 scaleUV(double uscale, double vscale)
	{
		for (int j=height-1;j>=0;j--)
			for (int i=width-1;i>=0;i--)
			{
				node[i][j].u*=uscale;
				node[i][j].v*=vscale;
			}
		return this;
	}
	
	public Grid8x8 addUV(double du, double dv)
	{
		for (int j=height-1;j>=0;j--)
			for (int i=width-1;i>=0;i--)
			{
				node[i][j].u+=du;
				node[i][j].v+=dv;
			}
		return this;
	}
	

	public double dist(double x1, double y1, double x2, double y2)
	// Returns the distance from to grid cells
	{
		int dim=Math.min(gridwidth,gridheight);
		double xx=(x1-x2)/dim;
		double yy=(y1-y2)/dim;
		return Math.sqrt(xx*xx+yy*yy);
	}

	public double radius(double x, double y)
	// Returns the distance from the grid cell to the grid center
	{
		return dist(x,y,gridwidth/2,gridheight/2);
	}

	public int crop(int n, int min, int max)
	{
		return (n<min)? min : (n>max)? max : n;
	}
}