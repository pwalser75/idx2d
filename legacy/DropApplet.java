import java.awt.*;
import java.applet.*;

import idx2d.*;
import idx2d.grid.*;
import idx2d.physics.Oscillator;

public class DropApplet extends ThreadApplet
{
	Texture source,output;
	Grid8x8 grid;
	GridDistorter distorter;
	long time=0;
	Oscillator ampOscillator;
	Oscillator wavesOscillator;
	
	boolean mouseInside=false;
	
	public void init()
	{
		setBackground(new Color(getHexParameter("background",0xFFFFFF)));
		source=getTexture("texture");
		
		grid=new IsoGrid8x8(size().width,size().height);
		
		boolean bilinear=getStringParameter("bilinear","true").equals("true");
		
		if (bilinear)
			distorter=new BilinearGridDistorter(size().width,size().height);
		else
			distorter=new FastGridDistorter(size().width,size().height);
			
		System.out.println("Bilinear: "+bilinear);
		
		double interval=getDoubleParameter("interval",10000);
		double minwave=getDoubleParameter("minwave",3);
		double maxwave=getDoubleParameter("maxwave",9);
		double minamp=getDoubleParameter("minamp",0);
		double maxamp=getDoubleParameter("maxamp",0.32);
		
		ampOscillator=new Oscillator(minamp,maxamp,interval);
		wavesOscillator=new Oscillator(maxwave,minwave,1.3*interval);
	}
	
	public void run()
	{
		while(true)
		{
			sinDistort();
			blitTexture(output);
			if (mouseInside) showStatus("DropApplet (c)2001 by Peter Walser [www2.active.ch/proxima]");
		}
	}
	
	public void sinDistort()
	{
		time=System.currentTimeMillis();
		double amp=ampOscillator.getValue(time);
		double waves=wavesOscillator.getValue(time);
		
		grid.reset();
		grid=Drop.create(grid,amp,waves,((double)time)/300d);
		
		output=distorter.distort(source,grid);
		
	}		
	
	public boolean mouseEnter(Event evt, int x, int y)
	{
		mouseInside=true;
		return true;
	}
	
	public boolean mouseExit(Event evt, int x, int y)
	{
		mouseInside=false;
		return true;
	}
	
}
