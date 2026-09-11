package idx2d;

import java.awt.*;
import java.applet.*;

public abstract class ThreadApplet extends Applet implements Runnable
{
	Thread animationThread;
	private boolean running=false;
		
	public void start()
	{
		animationThread=new Thread(this,"Animation Thread");
		running=true;
		animationThread.start();
	}
	
	public void stop()
	{
		running=false;
		animationThread.stop();
		animationThread=null;
	}
	
	public boolean isRunning()
	{
		return running;
	}
	
	public void blitTexture(Texture t)
	{
		Graphics g=getGraphics();
		if ((t!=null) && (g!=null))
			g.drawImage(t.getImage(),0,0,null);
	}
	
	public abstract void run();
	
	protected Texture getTexture(String name)
	{
		return new Texture(getImage(getDocumentBase(),getParameter(name)));
	}
	
// Parameter retreival
	
	protected String getStringParameter(String paramName, String paramDefault)
	{
		String newParam=getParameter(paramName);
		if (newParam==null) return paramDefault;
		return newParam;
	}

	protected int getIntParameter(String paramName, int paramDefault)
	{
		String newParam=getParameter(paramName);
		if (newParam==null) return paramDefault;
		try { return Integer.parseInt(newParam); }
		catch(Exception e){ return paramDefault; }
	}

	protected int getHexParameter(String paramName, int paramDefault)
	{
		String newParam=getParameter(paramName);
		if (newParam==null) return paramDefault;
		try { return Integer.parseInt(newParam,16); }
		catch(Exception e){ return paramDefault; }
	}

	protected float getFloatParameter(String paramName, float paramDefault)
	{
		return (float)getDoubleParameter(paramName,paramDefault);
	}

	protected double getDoubleParameter(String paramName, double paramDefault)
	{
		String newParam=getParameter(paramName);
		if (newParam==null) return paramDefault;
		try { return (Double.valueOf(newParam)).doubleValue(); }
		catch(Exception e){ return paramDefault; }
	}		
	
}
