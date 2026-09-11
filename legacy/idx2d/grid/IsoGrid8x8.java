package idx2d.grid;
import idx2d.*;

public class IsoGrid8x8 extends Grid8x8
{
	
	public IsoGrid8x8(int w, int h)
	{
		super(w,h);
	}

	public void reset()
	{
		int dim=Math.min(gridwidth,gridheight);
		for (int j=height-1;j>=0;j--)
			for (int i=width-1;i>=0;i--)
			{
				node[i][j].u=(double)i/gridwidth;
				node[i][j].v=(double)j/gridheight;
				node[i][j].intensity=255;
			}
	}
	
	public double getUMax()
	{
		return (double)width/gridwidth;
	}
	
	public double getVMax()
	{
		return (double)height/gridheight;
	}
	
	
	public void convert(Texture t)
	{
		double xscale=65536d*t.width;
		double yscale=65536d*t.height;
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
	
	public double dist(double x1, double y1, double x2, double y2)
	// Returns the distance from to grid cells
	{
		int dim=Math.min(gridwidth,gridheight);
		double xx=(x1-x2)/gridwidth;
		double yy=(y1-y2)/gridheight;
		return Math.sqrt(xx*xx+yy*yy);
	}
}