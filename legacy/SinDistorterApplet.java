import java.awt.*;
import java.applet.*;

import idx2d.*;
import idx2d.grid.*;
import idx2d.physics.Oscillator;

public class SinDistorterApplet extends ThreadApplet
{
	Texture texture;
	Texture output;
	Grid8x8 grid;
	GridDistorter distorter;
	long time=0;
	Oscillator angleOscillator;
	
	public void init()
	{
		texture=getTexture("texture");
		
		grid=new Grid8x8(size().width,size().height);
		distorter=new FastGridDistorter(size().width,size().height);
		angleOscillator=new Oscillator(-480,480,20000);
	}
	
	public void run()
	{
		while(true)
		{
			sinDistort();
			blitTexture(output);
		}
	}
	
	public void sinDistort()
	{
		time+=50;
				
		double angle=angleOscillator.getValue(time);
		
		grid.reset();
		grid=RotoZoomer.rotoZoom(grid,angle,1);
		
		output=distorter.distort(texture,SinDistorter.distort(grid,0.04,3,0,0.04,2,0));
		
	}		
}
