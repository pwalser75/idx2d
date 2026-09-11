import java.awt.*;
import java.applet.*;

import idx2d.*;
import idx2d.filter.*;
import idx2d.grid.*;
import idx2d.physics.Oscillator;

public class FeedbackApplet extends ThreadApplet
{
	Texture output,texture;
	Grid8x8 grid;
	GridDistorter distorter;
	long time=0;
	Oscillator angleOscillator,zoomOscillator,oscx,oscy;
	FastBlur fastBlur;
	
	public void init()
	{
		double w=(double)size().width;
		double h=(double)size().height;
		texture=getTexture("texture");
		grid=new Grid8x8(size().width,size().height);
		distorter=new FastGridDistorter(size().width,size().height);
		zoomOscillator=new Oscillator(0.92,0.96,5000);
		angleOscillator=new Oscillator(-0,0,16000);
		oscx=new Oscillator(0.2,0.8,8999);
		oscy=new Oscillator(0.2,0.8,13111);
	
		texture.resize(size().width,size().height);
		output=new Texture(size().width,size().height);
		fastBlur=new FastBlur(output);
	}
	
	public void run()
	{
		while(true)
		{
			time=System.currentTimeMillis();
			mix();
			blitTexture(output);
			distort();
			blur();
			//blur();
		}
	}
	
	private void distort()
	{	
		double zoom=zoomOscillator.getValue(time);
		double angle=angleOscillator.getValue(time);
		double x=oscx.getValue(time);
		double y=oscy.getValue(time);
		
		output=distorter.distort(output,RotoZoomer.rotoZoom(grid,angle,zoom,x,y));
	}
	
	private void mix()
	{
		for (int i=texture.pixel.length-1;i>=0;i--)
			output.pixel[i]=Color24.mix(output.pixel[i],texture.pixel[i],192);
			//output.pixel[i]=Color24.add(output.pixel[i],Color24.sub(texture.pixel[i],(output.pixel[i]&0xFEFEFE)>>1));
			//output.pixel[i]=Color24.mix(output.pixel[i],Color24.add(((output.pixel[i]&0xFCFCFC)>>2),(texture.pixel[i]&0xFCFCFC)>>2));
	}
		
	private void blur()
	{
		output=fastBlur.blur(output);
	}

}
