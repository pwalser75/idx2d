import java.awt.*;
import java.applet.*;

import idx2d.*;
import idx2d.grid.*;
import idx2d.physics.Oscillator;

public class LiquidApplet extends ThreadApplet
{
	private Texture output;
	private Texture source;
	private Grid8x8 grid;
	private GridDistorter distorter;
	private long time=0;
	private Oscillator oscX;
	private Oscillator oscY;
	private double intervalX=3000;
	private double intervalY=2000;
	private boolean autodrop=true;
	private boolean random=false;
	private double pressure;
	private double dropsize;
	private double speedfactor;
	private long earliestNextDropTime;
	
	public void init()
	{
		source=getTexture("texture");
		speedfactor=getDoubleParameter("speedfactor",1);
		pressure=getDoubleParameter("pressure",0.64);
		dropsize=getDoubleParameter("dropsize",1.44);
		
		grid=new IsoGrid8x8(size().width,size().height);
		distorter=new PhongGridDistorter(getTexture("envmap"),getTexture("lightmap"),size().width,size().height);
		
		oscX=new Oscillator(50,size().width-50,intervalX/speedfactor);
		oscY=new Oscillator(50,size().height-50,intervalY/speedfactor);
	}
	
	public void run()
	{
		while(isRunning())
		{
			applyEffect();
			blitTexture(output);
			try { Thread.sleep(10); }
			catch(Exception ignored){}
		}
	}
	
	public void applyEffect()
	{
		
		time=System.currentTimeMillis();
		if (autodrop) 
		{
			if (random) 
			{
				if (time%200==0) setDrop(20+(int)(Math.random()*(getSize().width-40)),20+(int)(Math.random()*(getSize().height-40)),pressure*2*Math.random());
			}
			else setDrop((int)oscX.getValue(time),(int)oscY.getValue(time),pressure);
		}
		GridOscillator.oscillate(grid,1,0.95);
		
		output=distorter.distort(source,grid);
	}		
	
	public void setDrop(int gridx, int gridy, double pressure)
	{
		gridx/=8;
		gridy/=8;
			
		int dx,dy;
		double dist;
		
		for (int x=0; x<grid.width; x++)
		{
			for (int y=0; y<grid.height; y++)
			{
				dx=gridx-x;
				dy=gridy-y;
				dist=Math.sqrt(dx*dx+dy*dy);
				grid.node[x][y].z-=pressure*Math.exp(-(dist*dist)/(dropsize*dropsize));
			}
		}
	}
	
	public boolean mouseEnter(Event evt, int x, int y)
	{
		autodrop=false;
		return true;
	}
	
	public boolean mouseExit(Event evt, int x, int y)
	{
		autodrop=true;
		return true;
	}
	
	public boolean mouseDown(Event evt, int x, int y)
	{
		setDrop(x,y,pressure*2);
		return true;
	}
	
	public boolean mouseDrag(Event evt, int x, int y)
	{
		setDrop(x,y,0.4*pressure);
		return true;
	}
	
	public boolean keyDown(Event evt, int key)
	{
		if ((char)key=='m') random=!random;
		return true;
	}
	
	
}
