import java.awt.*;
import java.applet.*;

import idx2d.*;

public class LakeApplet extends ThreadApplet
{
	Texture texture;
	Texture output;
	double wavesize;
	double periodTime;
	double waves;
	Image image;
	
	public void init()
	{
		double maxamp=getDoubleParameter("maxamp",10);
		wavesize=maxamp/(size().height/2);
		waves=getDoubleParameter("waves",8);
		periodTime=getDoubleParameter("T",1000);
		
		image=getImage(getDocumentBase(),getParameter("image"));
		texture=new Texture(image);
		
		output=new Texture(texture.width, texture.height);
		setBackground(java.awt.Color.black);
	}
	
	public void run()
	{
		while(true)
		{
			createLake();
			getGraphics().drawImage(image,0,0,null);
			getGraphics().drawImage(output.getImage(),0,texture.height,null);
		}
	}
	
	public void createLake()
	{
		int y2;
		double omega=2d*Math.PI/periodTime;
		double time=(double)System.currentTimeMillis();
		int width=texture.width;
		int height=texture.height;
		int sourceOffset,destOffset;
		
		for(int y=0;y<height;y++)
		{
			y2=(height-1-y)+(int)((double)y*wavesize*Math.sin(omega*time+waves*(double)(height-y)/(double)y));
			y2=Math.max(y2,0);
			y2=Math.min(y2,height-1);
			sourceOffset=y2*width;
			destOffset=y*width;
			
			for(int x=0;x<width;x++)
				output.pixel[x+destOffset]=Color24.mix(output.pixel[x+destOffset],texture.pixel[x+sourceOffset]);
		}
	}
}
