import java.awt.*;
import java.applet.*;

import idx2d.*;
import idx2d.grid.*;
import idx2d.physics.Oscillator;

public class DropBlenderApplet extends ThreadApplet
{
	TextureBlender textureBlender;
	Texture output;
	Grid8x8 grid;
	GridDistorter distorter;
	long startTime;
	Oscillator ampOscillator;
	Oscillator blendOscillator;
	Oscillator wavesOscillator;
	
	public void init()
	{
		textureBlender=new TextureBlender(getTexture("texture1"),getTexture("texture2"));
		
		grid=new IsoGrid8x8(size().width,size().height);
		distorter=new FastGridDistorter(size().width,size().height);
		
		double interval=getDoubleParameter("interval",10000);
		ampOscillator=new Oscillator(0,0.4,interval);
		wavesOscillator=new Oscillator(9,4,0.8*interval);
		blendOscillator=new Oscillator(0,255,2*interval);
	}
	
	public void run()
	{
		startTime=System.currentTimeMillis();
		while(true)
		{
			sinDistort();
			blitTexture(output);
		}
	}
	
	public void sinDistort()
	{
		long time=System.currentTimeMillis()-startTime;
		double amp=ampOscillator.getValue(time);
		int alpha=(int)blendOscillator.getValue(time);
		double waves=wavesOscillator.getValue(time);
		
		grid.reset();
		grid=Drop.create(grid,amp,waves,((double)time)/300d);
		
		output=distorter.distort(textureBlender.blend(alpha),grid);
		
	}		
}
