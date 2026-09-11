package idx2d.struct;

import idx2d.*;
import java.awt.Image;

public class BumpMap extends Texture
{	
	public BumpMap(int width, int height)
	{
		super(width,height);
		convertToGray();
	}
	
	public BumpMap(Image img)
	{
		super(img);
		convertToGray();
	}
	
	public BumpMap(String imageName)
	{
		super(imageName);
		convertToGray();
	}
	
	private void convertToGray()
	{
		for (int i=width*height-1;i>=0;i--)
			pixel[i]=Color24.getGray(pixel[i])&0xFF;
	}

	public int[] getDxMap(int scale)
	{
		int[] map=new int[width*height];
		int base;
		for (int y=height-1;y>=0;y--)
		{
			base=y*width;
			for (int x=width-1;x>=0;x--)
			{
				map[x+base]=(scale*(pixel[(x-1+width)%width+base]-pixel[(x+1)%width+base]))>>8;
			}
		}
		return map;
	}
	
	public int[] getDyMap(int scale)
	{
		int[] map=new int[width*height];
		int base;
		for (int y=height-1;y>=0;y--)
		{
			base=y*width;
			for (int x=width-1;x>=0;x--)
			{
				map[x+base]=(scale*(pixel[x+((y-1+height)%height)*width]-pixel[x+((y+1)%height)*width]))>>8;
			}
		}
		return map;
	}
	
}