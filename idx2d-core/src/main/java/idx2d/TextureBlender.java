package idx2d;

public class TextureBlender
{
	private Texture t1,t2,blended;
	int cacheIndicator=0xFFFFFFFF;
	
	public TextureBlender(Texture t1, Texture t2)
	{
		if (!t1.compatibleWith(t2)) t2.resize(t1.width,t1.height);
		this.t1=t1;
		this.t2=t2;
		blended=new Texture(t1.width,t1.height);
	}
	
	public Texture blend(int value)
	{
		int val=value<0 ? 0 : value>255 ? 255 : value;
		if (cacheIndicator==val/4) return blended;
		cacheIndicator=val/4;
		int[] pixel1=t1.pixel;
		int[] pixel2=t2.pixel;
		int[] target=blended.pixel;
		int r,g,b,c1,c2;
		int antival=255-val;
		
		int r1, g1, b1, r2, g2, b2;

		for (int i=t1.width*t1.height-1;i>=0;i--)
		{
			c1=pixel1[i];
			c2=pixel2[i];

			r1 = (c1 >> 16) & 255;
			g1 = (c1 >> 8)  & 255;
			b1 =  c1        & 255;

			r2 = (c2 >> 16) & 255;
			g2 = (c2 >> 8)  & 255;
			b2 =  c2        & 255;

			// dest = dest + (source - dest) * alpha
			r = r2 + (((r1 - r2) * val) >> 8);
			g = g2 + (((g1 - g2) * val) >> 8);
			b = b2 + (((b1 - b2) * val) >> 8);

			target[i]=0xFF000000|(r<<16)|(g<<8)|b;
		}
		return blended;
	}
	
	
}