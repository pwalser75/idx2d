// | -----------------------------------------------------------
// | FastBlur is (c)1999 by Peter Walser
// | Simple fast blur filter
// | -----------------------------------------------------------
// | Version: 1.0
// | Build:   01.04.1999
// | 
// | Peter Walser
// | proxima@active.ch
// | http://www2.active.ch/~proxima
// | -----------------------------------------------------------

package idx2d.filter;
import idx2d.Texture;

public class FastBlur
{
	public int width;
	public int height;
	public int size;
	public int pixel[];
	public int source[];
	private int yoffset;
	final int bitmask=0xFCFCFC;
	final int alpha=0xFF000000;

	// M E T H O D S
	
		public FastBlur(Texture origin)
		{
			width=origin.width;
			height=origin.height;
			source=new int[width*height];
			size=width*height;
			pixel=new int[width*height];
		}

		public Texture blur(Texture texture)
		{
			System.arraycopy(texture.pixel,0,source,0,width*height);
			for (int i=width*height-1;i>=0;i--) source[i]=(source[i]&bitmask)>>2;

			yoffset=width;
			for (int y=1;y<height-1;y++)
			{
				for (int x=1;x<width-1;x++) pixel[yoffset+x]=alpha|(source[yoffset+x-1]+source[yoffset+x+1]+source[yoffset+x-width]+source[yoffset+x+width]);
				yoffset+=width;
			}
			return new Texture(width,height,pixel);
		}
}