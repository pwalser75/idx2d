import java.awt.*;
import java.applet.*;

import idx2d.*;
import idx2d.grid.*;
import idx2d.physics.Oscillator;

public class DistorterApplet extends ThreadApplet
{
	private Texture output;
	private Texture source;
	private Grid8x8 grid;
	private GridDistorter distorter;
	private long time=0;
	private Oscillator oscX;
	private Oscillator oscY;
	private double intervalX=8000;
	private double intervalY=13000;
	private double rot=0;
	private double zoomfact=1;
	private final static double deg2rad=3.14159265/180;
	
	public void init()
	{
		source=getTexture("texture");
		
		grid=new IsoGrid8x8(size().width,size().height);
		//distorter=new PhongGridDistorter(getTexture("envmap"),getTexture("lightmap"),size().width,size().height);
		distorter=new FastGridDistorter(size().width,size().height);
		
		oscX=new Oscillator(50,size().width-50,intervalX);
		oscY=new Oscillator(50,size().height-50,intervalY);
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
		double rot2=Math.sin(rot*deg2rad)*360;
		double sin=Math.sin(rot2*deg2rad)*zoomfact;
		double cos=Math.cos(rot2*deg2rad)*zoomfact;
		
		for(int j=0;j<grid.height;j++)
		{
			ydist=((double)j-py)/grid.height;
			for (int i=0;i<grid.width;i++)
			{
				xdist=((double)i-px)/grid.width;
				grid.node[i][j].u=xdist*sin-ydist*cos;
				grid.node[i][j].v=xdist*cos+ydist*sin;
			}
		}
		zoomfact=Math.sin(rot*deg2rad)+1.2;

		for(int j=0;j<grid.height;j++)
		{
			ydist=((double)j/grid.height)-0.5;
			for (int i=0;i<grid.width;i++)
			{
				xdist=((double)i/grid.width)-0.5;
				dist=Math.sqrt(xdist*xdist+ydist*ydist);
				grid.node[i][j].u*=dist*Math.sin(rot2*deg2rad*dist);
				grid.node[i][j].v*=Math.cos(2*rot2*deg2rad*dist);
				grid.node[i][j].z=1+8*grid.radius(i,j)*Math.exp(-Math.sin(0.00001*time));
				
			}
		}
		rot=(rot+1)%360;
		output=distorter.distort(source,grid);
	
	}
}
