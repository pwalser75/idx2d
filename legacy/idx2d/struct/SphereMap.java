package idx2d.struct;

import idx2d.*;

public final class SphereMap extends Texture
{

	// C O N S T R U C T O R S

		public SphereMap()
		{
			this(128);
		}
		
		public SphereMap(int radius)
		{
			super(radius,radius);
			buildSphereMap(radius);
		}

	// M E T H O D S

		private void buildSphereMap(int radius)
		{
			float nx;
			float ny;
			float nz;
			int offset=radius*radius;
			for (int j=radius-1;j>=0;j--)
			{
				ny=(float)j/(float)radius;
				offset-=radius;

				for (int i=radius-1;i>=0;i--)
				{
					nx=(float)i/(float)radius;
					nz=(float)(1-Math.sqrt(nx*nx+ny*ny));
					if (nz<0) nz=0;
					pixel[i+offset]=(int)(255*nz);
				}
			}
		}		

}