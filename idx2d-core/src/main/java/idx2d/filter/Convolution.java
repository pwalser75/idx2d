package idx2d.filter;

import idx2d.*;

public class Convolution
{
	public static Texture apply(float[][] kernel, Texture t)
	{
		Texture nt=t.getClone();
		int xsize=kernel.length;
		int ysize=kernel[0].length;
		int cx=xsize/2;
		int cy=ysize/2;
		
		int r,g,b,p;
		int w=t.width-1;
		int h=t.height-1;
		for (int y=0; y<t.height; y++)
		{
			for (int x=0; x<t.width; x++)
			{
				r=g=b=0;
				
				for (int i=0; i<xsize; i++)
				{
					for (int j=0; j<ysize; j++)
					{
						p=t.pixel[clamp(x-cx+i,0,w)+clamp(y-cy+j,0,h)*t.width];
						r+=kernel[i][j]*Color24.getRed(p);
						g+=kernel[i][j]*Color24.getGreen(p);
						b+=kernel[i][j]*Color24.getBlue(p);
					}
				}
				r=clamp(r,0,255);
				g=clamp(g,0,255);
				b=clamp(b,0,255);
				nt.pixel[x+y*nt.width]=Color24.getColor(r,g,b);
			}
		}
		
		return nt;
	}
	
	private final static int clamp(int a, int min, int max)
	{
		return (a<min)?min:(a>max?max:a);
	}
	
	public static float[][] createGaussianKernel(float r)
	{
		int size=(int)(r*2+0.5)+1;
		return createGaussianKernel(r,size);
	}
	
	public static float[][] createGaussianKernel(float r, int size)
	{
		float[][] k= new float[size][size];
		float sum=0;
		
		for (int y=0; y<size; y++)
			for (int x=0; x<size; x++)
			{
				float dx=x-size/2;
				float dy=y-size/2;
				k[x][y]=(float)Math.exp(-(dx*dx+dy*dy)/r/2);
				sum+=k[x][y];
			}
		
		for (int y=0; y<size; y++)
			for (int x=0; x<size; x++) k[x][y]/=sum;
			
		return k;
	}
	
	public static void normalizeKernel(float[][] kernel)
	{
		float sum=0;
		for (int y=0; y<kernel.length; y++)
			for (int x=0; x<kernel[y].length; x++) sum+=kernel[x][y];
		
		float invsum=1f/sum;
		for (int y=0; y<kernel.length; y++)
			for (int x=0; x<kernel[y].length; x++) kernel[x][y]*=invsum;
	}
	
}
