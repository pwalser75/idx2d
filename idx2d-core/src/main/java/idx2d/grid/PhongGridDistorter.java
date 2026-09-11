package idx2d.grid;

import idx2d.*;
import idx2d.struct.*;


public class PhongGridDistorter implements GridDistorter
{
	public int width=0;
	public int height=0;
	private int toleranceX,toleranceY;
	
	private Grid8x8 grid;
	private int source[];	
	private int pixel[];

	private int offset;
	private int xpos,ypos,tx,ty,tw,th,intensity,nx,ny;
	private int dtx,dty,di,dnx,dny;
	private int uL1,uL2,uR1,uR2,duL,duR,uL,uR;
	private int vL1,vL2,vR1,vR2,dvL,dvR,vL,vR;
	private int nxL1,nxL2,nxR1,nxR2,dnxL,dnxR,nxL,nxR;
	private int nyL1,nyL2,nyR1,nyR2,dnyL,dnyR,nyL,nyR;
	
	private int tw2,th2;

	private Texture distortedTexture;
	private int[] envmap;
	private int[] lightmap;

	// C O N S T R U C T O R S

		public PhongGridDistorter(Texture envmap, Texture lightmap,int w,int h)
		{
			envmap.resize(256,256);
			lightmap.resize(256,256);
			this.envmap=envmap.pixel;
			this.lightmap=lightmap.pixel;
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
			
			nxL1=grid.node[gridX][gridY].nx<<16;
			nxR1=grid.node[gridX+1][gridY].nx<<16;
			nxL2=grid.node[gridX][gridY+1].nx<<16;
			nxR2=grid.node[gridX+1][gridY+1].nx<<16;
			
			nyL1=grid.node[gridX][gridY].ny<<16;
			nyR1=grid.node[gridX+1][gridY].ny<<16;
			nyL2=grid.node[gridX][gridY+1].ny<<16;
			nyR2=grid.node[gridX+1][gridY+1].ny<<16;
			
			duL=uL2-uL1;
			duR=uR2-uR1;
			dvL=vL2-vL1;
			dvR=vR2-vR1;
			dnxL=(nxL2-nxL1)>>3;
			dnxR=(nxR2-nxR1)>>3;
			dnyL=(nyL2-nyL1)>>3;
			dnyR=(nyR2-nyR1)>>3;
			
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
			nxL=nxL1;
			nxR=nxR1;
			nyL=nyL1;
			nyR=nyR1;
			xpos=gridX<<3;
			ypos=gridY<<3;
			for(int i=0;i<8;i++)
			{
				renderLine(xpos,ypos+i,uL,uR,vL,vR,nxL,nxR,nyL,nyR);
				uL+=duL;
				uR+=duR;
				vL+=dvL;
				vR+=dvR;
				nxL+=dnxL;
				nxR+=dnxR;
				nyL+=dnyL;
				nyR+=dnyR;
			}
		}

		private void renderLine(int x, int y, int txL, int txR, int tyL, int tyR, int nxL, int nxR, int nyL, int nyR)
		{
			offset=y*width+x;
			tx=(txL%(tw<<16))+(tw<<16);
			ty=(tyL%(th<<16))+(th<<16);
			
			nx=nxL;
			ny=nyL;
			dtx=txR-txL;
			dty=tyR-tyL;
			dnx=(nxR-nxL)/8;
			dny=(nyR-nyL)/8;
			if (dtx>tw2) dtx-=tw2<<1;
			if (dtx<-tw2) dtx+=tw2<<1;
			if (dty>tw2) dty-=tw2<<1;
			if (dty<-tw2) dty+=tw2<<1;
			
			dtx>>=3;
			dty>>=3;
			
			for(int i=0;i<8;i++)
			{
				pixel[offset+i]=Color24.multiply(source[((tx>>16)%tw)+((ty>>16)%th)*tw],lightmap[((nx>>16)+(((ny>>16)<<8)))&0xFFFF]);
				pixel[offset+i]=Color24.add(pixel[offset+i],(envmap[((nx>>16)+(((ny>>16)<<8)))&0xFFFF]&0xFEFEFE)>>1);
				tx+=dtx;
				ty+=dty;
				nx+=dnx;
				ny+=dny;
			}
			
		}
		
		private int crop(int a, int min, int max)
		{
			return (a<min)?min:(a>max)?max:a;
		}
}