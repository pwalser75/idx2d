package idx2d.grid;
import idx2d.*;

public class GouraudGridDistorter implements GridDistorter
{
	public int width=0;
	public int height=0;
	private int toleranceX,toleranceY;
	
	private Grid8x8 grid;
	private int source[];	
	private int pixel[];

	private int offset;
	private int xpos,ypos,tx,ty,tw,th,intensity;
	private int dtx,dty,di;
	private int uL1,uL2,uR1,uR2,duL,duR,uL,uR;
	private int vL1,vL2,vR1,vR2,dvL,dvR,vL,vR;
	private int iL1,iL2,iR1,iR2,diL,diR,iL,iR;
	
	private int tw2,th2;

	private Texture distortedTexture;

	// C O N S T R U C T O R S

		public GouraudGridDistorter(int w,int h)
		{
			width=w;
			height=h;
			pixel=new int[width*height];
			distortedTexture=new Texture(width,height,pixel);
		}

	// P U B L I C   M E T H O D S

		public Texture distort(Texture t, Grid8x8 g)
		{
			grid=g;
			tw=t.width;
			th=t.height;
			tw2=tw<<15;
			th2=th<<15;
			
			source=t.pixel;
			grid.convert(t);
			renderGrid();
			return distortedTexture;
		}

	// P R I V A T E   M E T H O D S

		private void renderGrid()
		{
			for (int n=grid.height-2;n>=0;n--)
				for (int m=grid.width-2;m>=0;m--) 
					renderArea(m,n);
		}

		private void renderArea(int gridX, int gridY)
		{
			uL1=grid.node[gridX][gridY].tx;
			uR1=grid.node[gridX+1][gridY].tx;
			uL2=grid.node[gridX][gridY+1].tx;
			uR2=grid.node[gridX+1][gridY+1].tx;

			vL1=grid.node[gridX][gridY].ty;
			vR1=grid.node[gridX+1][gridY].ty;
			vL2=grid.node[gridX][gridY+1].ty;
			vR2=grid.node[gridX+1][gridY+1].ty;
			
			iL1=grid.node[gridX][gridY].intensity<<16;
			iR1=grid.node[gridX+1][gridY].intensity<<16;
			iL2=grid.node[gridX][gridY+1].intensity<<16;
			iR2=grid.node[gridX+1][gridY+1].intensity<<16;

			duL=uL2-uL1;
			duR=uR2-uR1;
			dvL=vL2-vL1;
			dvR=vR2-vR1;
			diL=(iL2-iL1)>>3;
			diR=(iR2-iR1)>>3;
			if (duL>tw2) duL-=tw2<<1;
			if (duL<-tw2) duL+=tw2<<1;
			if (duR>tw2) duR-=tw2<<1;
			if (duR<-tw2) duR+=tw2<<1;
			if (dvL>tw2) dvL-=tw2<<1;
			if (dvL<-tw2) dvL+=tw2<<1;
			if (dvR>tw2) dvR-=tw2<<1;
			if (dvR<-tw2) dvR+=tw2<<1;
			
			duL>>=3;
			duR>>=3;
			dvL>>=3;
			dvR>>=3;
			
			uL=uL1;
			uR=uR1;
			vL=vL1;
			vR=vR1;
			iL=iL1;
			iR=iR1;
			xpos=gridX<<3;
			ypos=gridY<<3;
			for(int i=0;i<8;i++)
			{
				renderLine(xpos,ypos+i,uL,uR,vL,vR,iL,iR);
				uL+=duL;
				uR+=duR;
				vL+=dvL;
				vR+=dvR;
				iL+=diL;
				iR+=diR;
			}
		}

		private void renderLine(int x, int y, int txL, int txR, int tyL, int tyR, int iL, int iR)
		{
			offset=y*width+x;
			tx=(txL%(tw<<16))+(tw<<20);
			ty=(tyL%(th<<16))+(th<<20);
			intensity=iL;
			dtx=txR-txL;
			dty=tyR-tyL;
			di=(iR-iL)/8;
			if (dtx>tw2) dtx-=tw2<<1;
			if (dtx<-tw2) dtx+=tw2<<1;
			if (dty>tw2) dty-=tw2<<1;
			if (dty<-tw2) dty+=tw2<<1;
			
			dtx>>=3;
			dty>>=3;
			
			for(int i=0;i<8;i++)
			{
				pixel[offset+i]=Color24.scale(source[((tx>>16)%tw)+((ty>>16)%th)*tw],intensity>>16);
				tx+=dtx;
				ty+=dty;
				intensity+=di;
			}
			
		}
		
		private int crop(int a, int min, int max)
		{
			return (a<min)?min:(a>max)?max:a;
		}
}