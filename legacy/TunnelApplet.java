import java.awt.*;
import java.applet.*;

import idx2d.*;
import idx2d.grid.*;
import idx2d.physics.Oscillator;

public class TunnelApplet extends ThreadApplet
{
	Texture texture;
	Texture output;
	Grid8x8 grid;
	GouraudGridDistorter distorter;
	Oscillator angleOscillator;
	Oscillator speedOscillator;
	Oscillator fovOscillator;
	
	long time=0;
	double pos=0;
	boolean speedup=false;	
	long speedtime;
	long speedintervall=30000;
	
	public void init()
	{
		texture=getTexture("texture");
		
		grid=new Grid8x8(size().width,size().height);
		distorter=new GouraudGridDistorter(size().width,size().height);
		
		angleOscillator=new Oscillator(20,60,50000);
	}
	
	public void run()
	{
		while(true)
		{
			apply();
			blitTexture(output);
		}
	}
	
	public void apply()
	{
		time+=50;
		double angle=angleOscillator.getValue(time);
		if (speedup)
		{
			pos+=speedOscillator.getValue(speedtime);
			speedtime+=50;
			if (speedtime>speedintervall) speedup=false;
		}
		else pos+=0.016;
		double fov=speedup ? fovOscillator.getValue(speedtime) : 90;
		
		grid=Tunnel.create(grid,fov,angle,pos,0.5,1);
		output=distorter.distort(texture,grid);

	}

	public boolean mouseDown(Event evt, int x, int y)
	{
		if (speedup) return true;
		speedtime=0;
		speedOscillator=new Oscillator(0.016,0.42,speedintervall);
		fovOscillator=new Oscillator(90,179.99,speedintervall);
		speedup=true;
		return true;	
	}
		
}
