import java.awt.*;
import java.applet.*;

import idx2d.*;

public class RotationTest extends ThreadApplet
{
	Texture texture;
	Texture output;
	double angle=0;
	
	public void init()
	{
		texture=new Texture(getImage(getCodeBase(),"textures/horny.jpg"));
		angle=(double)Integer.parseInt(getParameter("angle"));
	}
	
	public void run()
	{
		output=texture.rotate(texture.width/2,texture.height/2,angle*Math.PI/180);
		blitTexture(output);
	}
}
