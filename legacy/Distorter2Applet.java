import java.awt.*;
import java.applet.*;

import idx2d.*;
import idx2d.grid.*;
import idx2d.filter.*;
import idx2d.physics.Oscillator;

public class Distorter2Applet extends ThreadApplet
{
	private Texture output;
	private Texture source;
	private Grid8x8 grid;
	private GridDistorter distorter;
	private long time=0;
	private Oscillator oscX;
	private Oscillator oscY;
	private Oscillator oscZ;
	private double intervalX=36997;
	private double intervalY=24989;
	private double rot=0;
	private final static double deg2rad=3.14159265/180;
	
	public void init()
	{
		source=Convolution.apply(Convolution.createGaussianKernel(2.5f,5),getTexture("texture"));
		
		grid=new IsoGrid8x8(size().width,size().height);
		distorter=new FastGridDistorter(size().width,size().height);
		
		oscX=new Oscillator(50,size().width-50,intervalX);
		oscY=new Oscillator(50,size().height-50,intervalY);
		oscZ=new Oscillator(0,1,5000);
	}
	
	public void run()
	{
		while(isRunning())
		{
			applyEffect();
			blitTexture(output);
		}
	}
	
	public void applyEffect()
	{
		
		time+=50;
		
		double px=oscX.getValue(time)/8;
		double py=oscY.getValue(time)/8;
		
		double xdist,ydist,dist;
		double zoomfact=Math.sin(rot*deg2rad)+1.2;
		double rot2=Math.sin(rot*deg2rad)*360;
		
		for(int j=0;j<grid.height;j++)
		{
			ydist=((double)j-py)/grid.height;
			for (int i=0;i<grid.width;i++)
			{
				xdist=((double)i-px)/grid.width;
				double twirl=64*Math.sin(8*grid.radius(i,j));
				double angle=(rot+twirl)*deg2rad;
				grid.node[i][j].u=xdist*Math.sin(angle)-ydist*Math.cos(angle);
				grid.node[i][j].v=xdist*Math.cos(angle)+ydist*Math.sin(angle);
				grid.node[i][j].z=6+3*(Math.sin(xdist*8)+Math.cos(ydist*8))*grid.radius(i,j)+2.4*Math.sin(angle);
			}
		}
		
		rot=(rot+1)%360;
		
		output=distorter.distort(source,grid);
	
	}
}
