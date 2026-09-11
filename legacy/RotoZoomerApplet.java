import java.awt.*;
import java.applet.*;

import idx2d.*;
import idx2d.grid.*;
import idx2d.physics.Oscillator;

public class RotoZoomerApplet extends ThreadApplet
{
	private Texture texture;
	private Texture output;
	private Grid8x8 grid;
	private GridDistorter distorter;
	private Oscillator zoomOscillator;
	private Oscillator angleOscillator;
	private long time=0;
	
	public void init()
	{
		texture=getTexture("texture");
		double minZoom=getDoubleParameter("minZoom",0.4);
		double maxZoom=getDoubleParameter("maxZoom",2);
		double angle=getDoubleParameter("angle",480);
		
		
		grid=new Grid8x8(size().width,size().height);
		distorter=new FastGridDistorter(size().width,size().height);
		
		zoomOscillator=new Oscillator(minZoom,maxZoom,8000);
		angleOscillator=new Oscillator(-angle,angle,20000);
	}
	
	public void run()
	{
		while(true)
		{
			rotoZoom();
			blitTexture(output);
		}
	}
	
	public void rotoZoom()
	{
		time+=50;
		
		double zoom=zoomOscillator.getValue(time);
		double angle=angleOscillator.getValue(time);
		
		output=distorter.distort(texture,RotoZoomer.rotoZoom(grid,angle,zoom,0.5,0.5));
		
	}		
}
